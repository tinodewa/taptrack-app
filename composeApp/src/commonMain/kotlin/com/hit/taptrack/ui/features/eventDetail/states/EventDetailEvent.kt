package com.hit.taptrack.ui.features.eventDetail.states

sealed class EventDetailEvent {
    object ManageEvent : EventDetailEvent()
    object ClearError : EventDetailEvent()
    object RefreshData : EventDetailEvent()

    // --- Alur Pendaftaran ---
    object ShowJoinDialog : EventDetailEvent()
    object HideJoinDialog : EventDetailEvent()
    data class RaceNumberChanged(val number: String) : EventDetailEvent()
    data class CategorySelected(val categoryId: String) : EventDetailEvent()
    object SubmitJoinEvent : EventDetailEvent()

    // FITUR BARU: Lihat Tiket
    object ShowTicketDialog : EventDetailEvent()
    object HideTicketDialog : EventDetailEvent()
}