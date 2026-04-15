package com.hit.taptrack.ui.features.myTicket.states

import com.hit.taptrack.data.model.TicketModel

data class MyTicketsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val tickets: List<TicketModel> = emptyList(),
    val selectedTicket: TicketModel? = null, // Tiket yang sedang dibuka QR-nya
    val errorMessage: String? = null
)