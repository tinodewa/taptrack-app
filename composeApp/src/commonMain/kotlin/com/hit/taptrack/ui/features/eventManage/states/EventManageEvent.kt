package com.hit.taptrack.ui.features.eventManage.states

import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.common.state.ParticipantStatus

sealed class EventManageEvent {
    object RefreshData : EventManageEvent()
    data class ChangeEventStatus(val newStatus: EventStatus) : EventManageEvent()
    data class OverrideParticipantStatus(
        val registrationId: String,
        val newStatus: ParticipantStatus
    ) : EventManageEvent()
}