package com.hit.taptrack.ui.features.eventDetail.states

sealed class EventDetailEvent {
    object JoinEvent : EventDetailEvent()
    object ManageEvent : EventDetailEvent()
    object ClearError : EventDetailEvent()
}