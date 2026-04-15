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

        // Mulai pemantauan lokal dan sinkronisasi jaringan
        observeEventDetail()
        syncEventDetailFromApi()

    }

    fun onEvent(event: EventDetailEvent) {
        when (event) {
            EventDetailEvent.RefreshData -> syncEventDetailFromApi(isRefresh = true)
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

    private fun observeEventDetail() {
        screenModelScope.launch {
            // UI State akan otomatis ter-update setiap kali ada perubahan di Room
            tapTrackRepository.getEventDetailFlow(eventId).collect { entity ->
                if (entity != null) {
                    val mappedDetail = EventDetailModel(
                        id = entity.id,
                        name = entity.title,
                        description = entity.descriptionText,
                        date = entity.startDatetime,
                        status = mapStringToStatus(entity.status),
                        checkpointCount = 0 // Jika tidak disimpan di EventEntity, bisa diabaikan atau ditarik dari CheckpointDao terpisah
                    )
                    _uiState.update { it.copy(isLoading = false, eventDetail = mappedDetail) }
                } else {
                    // Jika data belum ada sama sekali di lokal
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun syncEventDetailFromApi(isRefresh: Boolean = false) {
        screenModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            }
            // Jangan ubah isLoading menjadi true jika data sudah ada di lokal
            // agar layar tidak berkedip kosong saat me-refresh.
            if (_uiState.value.eventDetail == null && !isRefresh) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val result = tapTrackRepository.syncEventDetail(eventId)

            if (result is ResultState.Error) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = result.message) }
            } else {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
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