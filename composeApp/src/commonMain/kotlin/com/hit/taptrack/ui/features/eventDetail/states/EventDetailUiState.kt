package com.hit.taptrack.ui.features.eventDetail.states

import com.hit.taptrack.common.state.UserRole
import com.hit.taptrack.data.model.EventDetailModel
import com.hit.taptrack.data.model.TicketModel

data class EventDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val eventDetail: EventDetailModel? = null,
    val userRole: UserRole = UserRole.RACER, // Default simulasi: Racer

    // --- State Pendaftaran ---
    val showJoinDialog: Boolean = false,
    val raceNumberInput: String = "",
    val selectedCategoryId: String? = null,
    val isJoinLoading: Boolean = false,
    val isJoinSuccess: Boolean = false,
    val joinSuccessMessage: String? = null,

    // --- FITUR BARU: State Tiket Lokal ---
    val isRegistered: Boolean = false, // Menandakan apakah user sudah daftar
    val showTicketDialog: Boolean = false, // Menampilkan pop-up tiket
    val ticketData: TicketModel? = null, // Menyimpan data tiket jika ada

    val errorMessage: String? = null
)