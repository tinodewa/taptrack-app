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

    private fun loadEventData() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Simulasi GET data event & participants

            val dummyParticipants = listOf(
                ParticipantModel("REG-001", "Andi Gilang", "99", "FFA", ParticipantStatus.RACING),
                ParticipantModel("REG-002", "Doni Tata", "27", "FFA", ParticipantStatus.REGISTERED),
                ParticipantModel(
                    "REG-003",
                    "Mario Aji",
                    "16",
                    "Bebek 4T",
                    ParticipantStatus.FINISHED
                )
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    eventName = "Kejuaraan Daerah Motocross Jatim",
                    eventStatus = EventStatus.UPCOMING,
                    participants = dummyParticipants
                )
            }
        }
    }

    private fun updateEventStatus(newStatus: EventStatus) {
        screenModelScope.launch {
            _uiState.update { it.copy(isStatusChanging = true) }
            // TODO: API PUT /api/v1/events/:event_id/status
            delay(1000)
            _uiState.update { it.copy(isStatusChanging = false, eventStatus = newStatus) }
        }
    }

    private fun overrideParticipant(registrationId: String, newStatus: ParticipantStatus) {
        screenModelScope.launch {
            _uiState.update { it.copy(isOverrideLoading = true) }

            // Panggil dari Repository!
            val result = tapTrackRepository.overrideParticipantStatus(registrationId, newStatus.name)

            when (result) {
                is ResultState.Success -> {
                    // Update list dan tutup dialog
                    val updatedList = _uiState.value.participants.map {
                        if (it.registrationId == registrationId) it.copy(status = newStatus) else it
                    }
                    _uiState.update { it.copy(participants = updatedList, isOverrideLoading = false, selectedParticipant = null) }
                }
                is ResultState.Error -> {
                    // Tangani error, misal tampilkan lewat errorMessage
                    _uiState.update { it.copy(isOverrideLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}