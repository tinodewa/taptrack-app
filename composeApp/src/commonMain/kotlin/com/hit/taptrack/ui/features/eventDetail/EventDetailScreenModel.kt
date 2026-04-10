package com.hit.taptrack.ui.features.eventDetail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.common.state.UserRole
import com.hit.taptrack.data.model.EventDetailModel
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailEvent
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailUiState
import com.hit.taptrack.utils.helpers.UserSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailScreenModel(
    private val eventId: String,
    private val tapTrackRepository: TapTrackRepository,
    private val userSettings: UserSettings
) : ScreenModel {

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    init {
        // Ambil role dari UserSettings dan petakan ke Enum
        val roleString = userSettings.getRole()
        val mappedRole = when (roleString.uppercase()) {
            "COMMITTEE", "ADMIN" -> UserRole.COMMITTEE
            else -> UserRole.RACER // Default aman
        }

        _uiState.update { it.copy(userRole = mappedRole) }

        // Langsung muat data detail acara
        loadEventDetail()

    }

    fun onEvent(event: EventDetailEvent) {
        when (event) {
            EventDetailEvent.RefreshData -> loadEventDetail()
            EventDetailEvent.ShowJoinDialog -> _uiState.update {
                it.copy(
                    showJoinDialog = true,
                    raceNumberInput = ""
                )
            }

            EventDetailEvent.HideJoinDialog -> _uiState.update { it.copy(showJoinDialog = false) }
            is EventDetailEvent.RaceNumberChanged -> _uiState.update { it.copy(raceNumberInput = event.number) }
            EventDetailEvent.SubmitJoinEvent -> processJoinEvent()
            EventDetailEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            EventDetailEvent.ManageEvent -> {
                // TODO: Navigasi/Trigger ke fitur Kelola Acara (Committee Only)
            }
        }
    }

    private fun loadEventDetail() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch {
            val result = tapTrackRepository.getEventDetail(eventId)

            when (result) {
                is ResultState.Success -> {
                    val dto = result.data

                    // Format ulang array checkpoints menjadi string lokasi/deskripsi
                    val totalCheckpoints = dto.checkpoints.size

                    val mappedDetail = EventDetailModel(
                        id = dto.id ?: "",
                        name = dto.title ?: "Acara Tanpa Nama",
                        date = dto.startDatetime ?: "TBA",
                        description = dto.descriptionText ?: "Tidak ada deskripsi tersedia.",
                        location = "Sirkuit Utama ($totalCheckpoints Pos)",
                        status = mapStringToStatus(dto.status),
                        rules = "Rules here", // Placeholder, API belum menyediakan data ini
                        categories = List(totalCheckpoints) { index -> "Kategori Pos ${index + 1}" }, // Placeholder, API belum menyediakan data ini
                        registeredRacers = 50, // Placeholder, API belum menyediakan data ini
                        maxCapacity = 100 // Placeholder, API belum menyediakan data ini
                    )

                    _uiState.update { it.copy(isLoading = false, eventDetail = mappedDetail) }
                }

                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    private fun processJoinEvent() {
        val state = _uiState.value
        if (state.raceNumberInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nomor lambung tidak boleh kosong.") }
            return
        }

        _uiState.update { it.copy(isJoinLoading = true, errorMessage = null) }

        screenModelScope.launch {
            val result = tapTrackRepository.joinEvent(eventId, state.raceNumberInput)

            when (result) {
                is ResultState.Success -> {
                    // Sukses mendaftar!
                    _uiState.update {
                        it.copy(
                            isJoinLoading = false,
                            showJoinDialog = false,
                            isJoinSuccess = true,
                            // Pesan dari server, cth: "Registration successful"
                            joinSuccessMessage = result.data.message
                                ?: "Berhasil mendaftar balapan!"
                        )
                    }
                }

                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(
                            isJoinLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun mapStringToStatus(status: String?): EventStatus {
        return when (status?.uppercase()) {
            "UPCOMING" -> EventStatus.UPCOMING
            "ACTIVE", "LIVE" -> EventStatus.ACTIVE
            "COMPLETED", "FINISHED" -> EventStatus.FINISHED
            else -> EventStatus.UPCOMING
        }
    }
}