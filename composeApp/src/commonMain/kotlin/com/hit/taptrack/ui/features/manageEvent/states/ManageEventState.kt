package com.hit.taptrack.ui.features.manageEvent.states

import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.data.model.ParticipantModel

data class ManageEventUiState(
    val eventId: String = "",
    val eventName: String = "",
    val eventStatus: EventStatus = EventStatus.UPCOMING,
    val isLoading: Boolean = true,
    val isStatusChanging: Boolean = false,
    val participants: List<ParticipantModel> = emptyList(),
    val errorMessage: String? = null
)