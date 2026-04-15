package com.hit.taptrack.ui.features.eventManage.states

import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.data.model.EventDetailModel
import com.hit.taptrack.data.model.ParticipantModel

data class EventManageUiState(
    val eventId: String = "",
    val eventDetail: EventDetailModel? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isStatusChanging: Boolean = false,
    val participants: List<ParticipantModel> = emptyList(),

    // --- TAMBAHAN UNTUK OVERRIDE ---
    val selectedParticipant: ParticipantModel? = null, // Peserta yang sedang dibuka dialognya
    val isOverrideLoading: Boolean = false, // Indikator loading saat tombol override ditekan

    val errorMessage: String? = null
)