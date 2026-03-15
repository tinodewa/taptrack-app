package com.hit.taptrack.ui.features.manageEvent.states

import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.common.state.ParticipantStatus

sealed class ManageEventEvent {
    object RefreshData : ManageEventEvent()
    data class ChangeEventStatus(val newStatus: EventStatus) : ManageEventEvent()
    data class OverrideParticipantStatus(
        val registrationId: String,
        val newStatus: ParticipantStatus
    ) : ManageEventEvent()
}