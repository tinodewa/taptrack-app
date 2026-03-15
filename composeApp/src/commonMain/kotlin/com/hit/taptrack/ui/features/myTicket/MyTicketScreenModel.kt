package com.hit.taptrack.ui.features.myTicket

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.data.model.TicketModel
import com.hit.taptrack.ui.features.myTicket.states.MyTicketsEvent
import com.hit.taptrack.ui.features.myTicket.states.MyTicketsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyTicketsScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(MyTicketsUiState())
    val uiState: StateFlow<MyTicketsUiState> = _uiState.asStateFlow()

    init {
        loadTickets()
    }

    fun onEvent(event: MyTicketsEvent) {
        when (event) {
            is MyTicketsEvent.SelectTicket -> _uiState.update { it.copy(selectedTicket = event.ticket) }
            MyTicketsEvent.RefreshTickets -> loadTickets()
        }
    }

    private fun loadTickets() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Simulasi API GET /api/v1/registrations/my-events

            val dummyTickets = listOf(
                TicketModel(
                    registrationId = "REG-9912A",
                    eventName = "Kejuaraan Daerah Motocross Jatim",
                    date = "15 April 2026",
                    location = "Sirkuit Internasional Batu",
                    category = "Bebek Modif 4T 130cc",
                    racerNumber = "99",
                    qrCodePayload = "TAPTRACK-REG-9912A",
                    status = "REGISTERED"
                ),
                TicketModel(
                    registrationId = "REG-8821B",
                    eventName = "Enduro Challenge 2026",
                    date = "20 Mei 2026",
                    location = "Hutan Pinus, Mojokerto",
                    category = "Sport Trail 250cc Open",
                    racerNumber = "27",
                    qrCodePayload = "TAPTRACK-REG-8821B",
                    status = "VERIFIED"
                )
            )

            _uiState.update {
                it.copy(isLoading = false, tickets = dummyTickets)
            }
        }
    }
}