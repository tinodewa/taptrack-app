package com.hit.taptrack.ui.features.eventCreate

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.data.sources.remote.requestBody.CreateEventRequest
import com.hit.taptrack.data.sources.remote.responseBody.CheckpointDetail
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateEvent
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateUiState
import com.hit.taptrack.ui.features.eventCreate.states.StageInputModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventCreateScreenModel(
    private val tapTrackRepository: TapTrackRepository,
) : ScreenModel {

    private val _uiState = MutableStateFlow(EventCreateUiState())
    val uiState: StateFlow<EventCreateUiState> = _uiState.asStateFlow()

    fun onEvent(event: EventCreateEvent) {
        when (event) {
            is EventCreateEvent.NameChanged -> _uiState.update { it.copy(name = event.name) }
            is EventCreateEvent.DateChanged -> _uiState.update { it.copy(date = event.date) }
            is EventCreateEvent.LocationChanged -> _uiState.update { it.copy(location = event.location) }
            is EventCreateEvent.DescriptionChanged -> _uiState.update { it.copy(description = event.desc) }
            is EventCreateEvent.RulesChanged -> _uiState.update { it.copy(rules = event.rules) }
            is EventCreateEvent.MaxCapacityChanged -> _uiState.update { it.copy(maxCapacity = event.capacity) }

            is EventCreateEvent.AddCategory -> {
                val current = _uiState.value.categories.toMutableList()
                current.add(event.category)
                _uiState.update { it.copy(categories = current) }
            }
            is EventCreateEvent.RemoveCategory -> {
                val current = _uiState.value.categories.toMutableList()
                if (current.size > 1) current.removeAt(event.index) // Minimal 1 kelas
                _uiState.update { it.copy(categories = current) }
            }

            // --- LOGIKA SPECIAL STAGE ---
            EventCreateEvent.AddStage -> {
                val current = _uiState.value.stages.toMutableList()
                val nextStageNum = (current.maxOfOrNull { it.stageNumber } ?: 0) + 1
                current.add(
                    StageInputModel(
                        stageNumber = nextStageNum,
                        checkpoints = listOf("SS$nextStageNum Start", "SS$nextStageNum Finish")
                    )
                )
                _uiState.update { it.copy(stages = current) }
            }

            is EventCreateEvent.RemoveStage -> {
                val current = _uiState.value.stages.toMutableList()
                if (current.size > 1) { // Minimal harus ada 1 SS
                    current.removeAt(event.stageIndex)
                    _uiState.update { it.copy(stages = current) }
                }
            }

            is EventCreateEvent.AddCheckpointToStage -> {
                val currentStages = _uiState.value.stages.toMutableList()
                val targetStage = currentStages[event.stageIndex]
                val cps = targetStage.checkpoints.toMutableList()

                // Sisipkan tepat sebelum Garis Finish di stage tersebut
                val insertIndex = if (cps.size > 1) cps.size - 1 else cps.size
                cps.add(insertIndex, "Pos Tengah")

                currentStages[event.stageIndex] = targetStage.copy(checkpoints = cps)
                _uiState.update { it.copy(stages = currentStages) }
            }

            is EventCreateEvent.RemoveCheckpointFromStage -> {
                val currentStages = _uiState.value.stages.toMutableList()
                val targetStage = currentStages[event.stageIndex]
                val cps = targetStage.checkpoints.toMutableList()

                // Cegah penghapusan Start (0) dan Finish (terakhir)
                if (event.cpIndex > 0 && event.cpIndex < cps.size - 1) {
                    cps.removeAt(event.cpIndex)
                    currentStages[event.stageIndex] = targetStage.copy(checkpoints = cps)
                    _uiState.update { it.copy(stages = currentStages) }
                }
            }

            is EventCreateEvent.UpdateCheckpointName -> {
                val currentStages = _uiState.value.stages.toMutableList()
                val targetStage = currentStages[event.stageIndex]
                val cps = targetStage.checkpoints.toMutableList()

                cps[event.cpIndex] = event.newName
                currentStages[event.stageIndex] = targetStage.copy(checkpoints = cps)
                _uiState.update { it.copy(stages = currentStages) }
            }

            EventCreateEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            EventCreateEvent.SubmitEvent -> submitEvent()
        }
    }

    private fun submitEvent() {
        val state = _uiState.value

        // Validasi Dasar
        if (state.name.isBlank() || state.date.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama dan Tanggal wajib diisi.") }
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Merakit Checkpoints Enduro (SANGAT PENTING!)
            var globalSequenceOrder = 1 // Urutan tidak pernah reset agar peladen tahu rute total
            val mappedCheckpoints = mutableListOf<CheckpointDetail>()

            state.stages.forEach { stage ->
                stage.checkpoints.forEachIndexed { index, checkpointName ->
                    val flag = when (index) {
                        0 -> "START"
                        stage.checkpoints.size - 1 -> "FINISH"
                        else -> "MIDDLE"
                    }

                    mappedCheckpoints.add(
                        CheckpointDetail(
                            checkpointName = checkpointName,
                            typeFlag = flag,
                            sequenceOrder = globalSequenceOrder++, // Naik terus lintas SS
                            stageNumber = stage.stageNumber
                        )
                    )
                }
            }

            // 2. Siapkan Request Body untuk Golang
            val request = CreateEventRequest(
                title = state.name,
                startDatetime = state.date,
                descriptionText = state.description,
                categories = state.categories, // Array kategori baru
                checkpoints = mappedCheckpoints // Array pos yang sudah mapan
            )

            // 3. Tembak ke Repository
            val result = tapTrackRepository.createNewEvent(request)

            when (result) {
                is ResultState.Success -> {
                    // Berhasil! UI akan membaca isSuccess = true dan menavigasi user kembali
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}