package com.hit.taptrack.ui.features.eventManage

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.common.state.ParticipantStatus
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.model.ParticipantModel
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.ui.features.eventManage.states.EventManageEvent
import com.hit.taptrack.ui.features.eventManage.states.EventManageUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventManageScreenModel(
    private val eventId: String,
    private val tapTrackRepository: TapTrackRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(EventManageUiState(eventId = eventId))
    val uiState: StateFlow<EventManageUiState> = _uiState.asStateFlow()

    init {
        observeParticipants()
        loadEventData()
    }

    fun onEvent(event: EventManageEvent) {
        when (event) {
            EventManageEvent.RefreshData -> loadEventData()
            is EventManageEvent.ChangeEventStatus -> updateEventStatus(event.newStatus)
            is EventManageEvent.SelectParticipant -> _uiState.update { it.copy(selectedParticipant = event.participant) }
            is EventManageEvent.OverrideParticipantStatus -> overrideParticipant(
                event.registrationId,
                event.newStatus
            )
        }
    }

    private fun observeParticipants() {
        screenModelScope.launch {
            tapTrackRepository.getParticipantsFlow(eventId).collect { entities ->
                val mapped = entities.map { entity ->
                    ParticipantModel(
                        registrationId = entity.registrationId,
                        racerName = entity.racerName,
                        racerNumber = entity.raceNumber,
                        category = entity.vehicleClass,
                        status = mapStringToParticipantStatus(entity.registrationStatus)
                    )
                }
                _uiState.update { it.copy(participants = mapped, isLoading = false) }
            }
        }
    }

    private fun loadEventData() {
        screenModelScope.launch {
            // UI shows loading if DB is empty
            if (_uiState.value.participants.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }

            // Sync from API
            val result = tapTrackRepository.syncParticipants(eventId)
            if (result is ResultState.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    private fun updateEventStatus(newStatus: EventStatus) {
        screenModelScope.launch {
            // Nyalakan indikator loading dan bersihkan error sebelumnya
            _uiState.update { it.copy(isStatusChanging = true, errorMessage = null) }

            // Panggil Repository! Gunakan nama enum sebagai string (misal: "ACTIVE")
            val result = tapTrackRepository.updateEventStatus(eventId, newStatus.name)

            when (result) {
                is ResultState.Success -> {
                    // Berhasil! Matikan loading dan perbarui UI dengan status baru
                    _uiState.update {
                        it.copy(
                            isStatusChanging = false,
                            eventStatus = newStatus
                        )
                    }
                }

                is ResultState.Error -> {
                    // Gagal, matikan loading dan tampilkan pesan error
                    _uiState.update {
                        it.copy(
                            isStatusChanging = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun overrideParticipant(registrationId: String, newStatus: ParticipantStatus) {
        screenModelScope.launch {
            _uiState.update { it.copy(isOverrideLoading = true) }

            // Panggil dari Repository!
            val result =
                tapTrackRepository.overrideParticipantStatus(registrationId, newStatus.name)

            when (result) {
                is ResultState.Success -> {
                    // Update list dan tutup dialog
                    val updatedList = _uiState.value.participants.map {
                        if (it.registrationId == registrationId) it.copy(status = newStatus) else it
                    }
                    _uiState.update {
                        it.copy(
                            participants = updatedList,
                            isOverrideLoading = false,
                            selectedParticipant = null
                        )
                    }
                }

                is ResultState.Error -> {
                    // Tangani error, misal tampilkan lewat errorMessage
                    _uiState.update {
                        it.copy(
                            isOverrideLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Fungsi bantu untuk memetakan String dari API/Room DB menjadi Enum.
     * Menggunakan .uppercase() agar aman dari masalah case-sensitive (misal: "registered" vs "REGISTERED").
     */
    fun mapStringToParticipantStatus(status: String?): ParticipantStatus {
        if (status == null) return ParticipantStatus.UNKNOWN

        return when (status.uppercase().trim()) {
            "REGISTERED" -> ParticipantStatus.REGISTERED
            "ON_TRACK", "RACING" -> ParticipantStatus.RACING
            "FINISHED", "COMPLETED" -> ParticipantStatus.FINISHED
            "DNF" -> ParticipantStatus.DNF
            "DSQ", "DISQUALIFIED" -> ParticipantStatus.DSQ
            else -> ParticipantStatus.UNKNOWN
        }
    }
}