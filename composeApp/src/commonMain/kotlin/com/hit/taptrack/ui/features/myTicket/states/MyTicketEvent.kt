package com.hit.taptrack.ui.features.myTicket.states

import com.hit.taptrack.data.model.TicketModel

sealed class MyTicketsEvent {
    object RefreshTickets : MyTicketsEvent()
    data class SelectTicket(val ticket: TicketModel?) : MyTicketsEvent()
}