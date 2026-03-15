package com.hit.taptrack.ui.features.leaderboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.RacerStatus
import com.hit.taptrack.data.model.LeaderboardEntryModel
import com.hit.taptrack.ui.features.leaderboard.states.LeaderboardEvent
import com.hit.taptrack.ui.features.leaderboard.states.LeaderboardUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardScreenModel(private val eventId: String) : ScreenModel {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun onEvent(event: LeaderboardEvent) {
        when (event) {
            is LeaderboardEvent.ChangeCategory -> {
                _uiState.update { it.copy(currentCategory = event.newCategory) }
                loadLeaderboard() // Muat ulang data sesuai kategori
            }
            LeaderboardEvent.RefreshLeaderboard -> loadLeaderboard()
        }
    }

    private fun loadLeaderboard() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1200) // Simulasi GET /api/v1/events/:event_id/leaderboard

            val dummyEntries = listOf(
                LeaderboardEntryModel(1, "99", "Andi Gilang", "Yamaha YZ250F", "01:24.350", "Leader",
                    RacerStatus.FINISHED, true),
                LeaderboardEntryModel(2, "27", "Doni Tata", "KTM 250 EXC-F", "01:25.120", "+0.770s", RacerStatus.FINISHED),
                LeaderboardEntryModel(3, "16", "Mario Aji", "Honda CRF250R", "01:26.005", "+1.655s", RacerStatus.FINISHED),
                LeaderboardEntryModel(4, "05", "Gerry Salim", "Kawasaki KX250", "01:26.900", "+2.550s", RacerStatus.RACING),
                LeaderboardEntryModel(5, "12", "Dimas Ekky", "Husqvarna FC 250", "-", "-", RacerStatus.DNF)
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    eventName = "Kejuaraan Daerah Motocross Jatim",
                    entries = dummyEntries
                )
            }
        }
    }
}