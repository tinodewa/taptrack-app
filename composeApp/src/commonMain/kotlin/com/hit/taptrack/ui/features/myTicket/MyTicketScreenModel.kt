package com.hit.taptrack.ui.features.myTicket

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.model.TicketModel
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.ui.features.myTicket.states.MyTicketsEvent
import com.hit.taptrack.ui.features.myTicket.states.MyTicketsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyTicketsScreenModel(
    private val tapTrackRepository: TapTrackRepository
) : ScreenModel {

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
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch {
            val result = tapTrackRepository.getMyTickets()

            when (result) {
                is ResultState.Success -> {
                    // Mapping dari DTO (EventsParticipation) ke Model UI (TicketModel)
                    val mappedTickets = result.data.map { dto ->
                        TicketModel(
                            // Kita gunakan eventId atau hash sebagai pengganti Registration ID jika tidak ada
                            registrationId = dto.eventId ?: "UNKNOWN",
                            eventName = dto.title ?: "Acara Tanpa Nama",
                            date = dto.startDatetime ?: "TBA",
                            // Fallback karena API tidak mengirim location dan category di endpoint ini
                            location = "Sirkuit Utama",
                            category = "Kelas Umum",
                            racerNumber = dto.raceNumber ?: "-",
                            qrCodePayload = dto.qrTokenHash ?: "", // Payload krusial untuk Scanner!
                            status = dto.registrationStatus ?: "REGISTERED"
                        )
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tickets = mappedTickets
                        )
                    }
                }
                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}