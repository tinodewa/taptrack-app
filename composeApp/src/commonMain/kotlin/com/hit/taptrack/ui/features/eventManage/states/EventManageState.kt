package com.hit.taptrack.ui.features.eventManage.states

import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.data.model.ParticipantModel

data class EventManageUiState(
    val eventId: String = "",
    val eventName: String = "",
    val eventStatus: EventStatus = EventStatus.UPCOMING,
    val isLoading: Boolean = true,
    val isStatusChanging: Boolean = false,
    val participants: List<ParticipantModel> = emptyList(),

    // --- TAMBAHAN UNTUK OVERRIDE ---
    val selectedParticipant: ParticipantModel? = null, // Peserta yang sedang dibuka dialognya
    val isOverrideLoading: Boolean = false, // Indikator loading saat tombol override ditekan

    val errorMessage: String? = null
)