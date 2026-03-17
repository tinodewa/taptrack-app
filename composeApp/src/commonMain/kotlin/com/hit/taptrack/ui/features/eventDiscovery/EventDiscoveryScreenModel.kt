package com.hit.taptrack.ui.features.eventDiscovery

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.data.model.EventModel
import com.hit.taptrack.ui.features.eventDiscovery.states.EventDiscoveryEvent
import com.hit.taptrack.ui.features.eventDiscovery.states.EventDiscoveryUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDiscoveryScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(EventDiscoveryUiState())
    val uiState: StateFlow<EventDiscoveryUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun onEvent(event: EventDiscoveryEvent) {
        when (event) {
            is EventDiscoveryEvent.TabSelected -> {
                _uiState.update { it.copy(selectedTabIndex = event.index) }
                applyFilter()
            }

            EventDiscoveryEvent.RefreshEvents -> loadEvents()
        }
    }

    private fun loadEvents() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch {
            // TODO: Ganti dengan pemanggilan ke repository GET /api/v1/events
            delay(1000) // Simulasi loading network

            val dummyData = listOf(
                EventModel(
                    "EVT-001",
                    "Kejuaraan Daerah Motocross Jatim",
                    "15 Apr 2026",
                    "Sirkuit Batu, Malang",
                    EventStatus.UPCOMING,
                    120
                ),
                EventModel(
                    "EVT-002",
                    "Enduro Challenge 2026",
                    "20 Mei 2026",
                    "Hutan Pinus, Mojokerto",
                    EventStatus.UPCOMING,
                    85
                ),
                EventModel(
                    "EVT-003",
                    "TapTrack Open Championship",
                    "15 Mar 2026",
                    "Sirkuit Bung Tomo, Surabaya",
                    EventStatus.ONGOING,
                    200
                ),
                EventModel(
                    "EVT-004",
                    "National Drag Bike",
                    "10 Feb 2026",
                    "Kenjeran Park, Surabaya",
                    EventStatus.COMPLETED,
                    350
                )
            )

            _uiState.update {
                it.copy(
                    allEvents = dummyData,
                    isLoading = false,
                    isCommittee = true // <-- Ubah ke true untuk mensimulasikan login sebagai Panitia
                )
            }
            applyFilter()
        }
    }

    private fun applyFilter() {
        val state = _uiState.value
        val statusFilter = when (state.selectedTabIndex) {
            0 -> EventStatus.UPCOMING
            1 -> EventStatus.ONGOING
            2 -> EventStatus.COMPLETED
            else -> EventStatus.UPCOMING
        }

        val filtered = state.allEvents.filter { it.status == statusFilter }
        _uiState.update { it.copy(filteredEvents = filtered) }
    }
}