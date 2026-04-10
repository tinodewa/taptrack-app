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
        // 1. Awasi perubahan di Database Lokal
        observeLocalTickets()
        // 2. Minta pembaruan dari API
        refreshData()
    }

    fun onEvent(event: MyTicketsEvent) {
        when (event) {
            is MyTicketsEvent.SelectTicket -> _uiState.update { it.copy(selectedTicket = event.ticket) }
            MyTicketsEvent.RefreshTickets -> refreshData()
        }
    }

    private fun observeLocalTickets() {
        screenModelScope.launch {
            tapTrackRepository.myTicketsFlow.collect { entities ->
                val mappedTickets = entities.map { entity ->
                    TicketModel(
                        registrationId = entity.eventId,
                        eventName = entity.title,
                        date = entity.startDatetime,
                        location = "Sirkuit Utama", // Fallback UI
                        category = "Kelas Umum", // Fallback UI
                        racerNumber = entity.raceNumber,
                        qrCodePayload = entity.qrTokenHash,
                        status = entity.registrationStatus
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false, // Matikan loading saat data lokal berhasil dibaca
                        tickets = mappedTickets
                    )
                }
            }
        }
    }

    private fun refreshData() {
        screenModelScope.launch {
            // Tampilkan loading HANYA jika list tiket masih kosong (belum ada cache)
            if (_uiState.value.tickets.isEmpty()) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val result = tapTrackRepository.syncMyTickets()

            // Tangani jika terjadi error jaringan saat sinkronisasi
            if (result is ResultState.Error) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // Hanya tampilkan pesan error jika data lokal juga kosong
                        errorMessage = if (_uiState.value.tickets.isEmpty()) result.message else null
                    )
                }
            }
        }
    }
}