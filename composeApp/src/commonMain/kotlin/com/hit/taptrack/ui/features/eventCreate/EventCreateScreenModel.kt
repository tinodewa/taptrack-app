package com.hit.taptrack.ui.features.eventCreate

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.data.sources.remote.requestBody.CreateEventRequest
import com.hit.taptrack.data.sources.remote.responseBody.CheckpointDetail
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateEvent
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateUiState
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

            is EventCreateEvent.AddCheckpoint -> {
                val current = _uiState.value.checkpoints.toMutableList()
                // Sisipkan sebelum elemen terakhir (Garis Finish)
                val insertIndex = if (current.size > 1) current.size - 1 else current.size
                current.add(insertIndex, event.name)
                _uiState.update { it.copy(checkpoints = current) }
            }
            is EventCreateEvent.RemoveCheckpoint -> {
                val current = _uiState.value.checkpoints.toMutableList()
                // Jangan izinkan menghapus index 0 (Start) atau index terakhir (Finish)
                if (event.index > 0 && event.index < current.size - 1) {
                    current.removeAt(event.index)
                    _uiState.update { it.copy(checkpoints = current) }
                }
            }
            is EventCreateEvent.UpdateCheckpoint -> {
                val current = _uiState.value.checkpoints.toMutableList()
                current[event.index] = event.newName
                _uiState.update { it.copy(checkpoints = current) }
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

            // 1. Merakit Checkpoints Dinamis
            val mappedCheckpoints = state.checkpoints.mapIndexed { index, checkpointName ->
                val flag = when (index) {
                    0 -> "START" // Elemen paling pertama selalu Start
                    state.checkpoints.size - 1 -> "FINISH" // Elemen paling akhir selalu Finish
                    else -> "MIDDLE" // Sisanya di tengah
                }

                CheckpointDetail(
                    checkpointName = checkpointName,
                    typeFlag = flag,
                    sequenceOrder = index + 1 // Urutan 1, 2, 3...
                )
            }

            // 2. Siapkan Request Body untuk Golang
            val request = CreateEventRequest(
                title = state.name,
                startDatetime = state.date,
                descriptionText = state.description,
                data = ArrayList(mappedCheckpoints) // Masukkan array checkpoint yang sudah dirakit
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