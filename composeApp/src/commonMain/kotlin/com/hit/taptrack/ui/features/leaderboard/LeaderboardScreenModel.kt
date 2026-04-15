package com.hit.taptrack.ui.features.leaderboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.RacerStatus
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.model.LeaderboardEntryModel
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.ui.features.leaderboard.states.LeaderboardEvent
import com.hit.taptrack.ui.features.leaderboard.states.LeaderboardUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardScreenModel(
    private val eventId: String,
    private val tapTrackRepository: TapTrackRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun onEvent(event: LeaderboardEvent) {
        when (event) {
            is LeaderboardEvent.ChangeCategory -> {
                _uiState.update { it.copy(currentCategory = event.newCategory) }
                // TODO: Jika API Golang mendukung filter kategori, kirimkan sebagai query param.
                // Untuk sekarang, kita muat ulang.
                loadLeaderboard()
            }
            LeaderboardEvent.RefreshLeaderboard -> loadLeaderboard(isRefresh = true)
        }
    }

    private fun loadLeaderboard(isRefresh: Boolean = false) {
        screenModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val result = tapTrackRepository.getLeaderboard(eventId)

            when (result) {
                is ResultState.Success -> {
                    val leaderboardList = result.data.leaderboard

                    // Ambil waktu dari pembalap urutan pertama sebagai basis perhitungan Gap
                    val leaderTimeMs = leaderboardList.firstOrNull { it.rank == 1 }?.elapsedTimeMs ?: 0

                    val mappedEntries = leaderboardList.map { dto ->
                        val timeMs = dto.elapsedTimeMs ?: 0

                        // 1. Format waktu tempuh
                        val timeString = if (timeMs > 0) formatTimeMs(timeMs) else "-"

                        // 2. Kalkulasi selisih waktu (Gap)
                        val gapString = when {
                            dto.rank == 1 -> "Leader"
                            timeMs > 0 && leaderTimeMs > 0 -> "+${formatTimeMs(timeMs - leaderTimeMs)}"
                            else -> "-"
                        }

                        LeaderboardEntryModel(
                            position = dto.rank ?: 99,
                            racerNumber = dto.raceNumber ?: "-",
                            racerName = dto.racerName ?: "Unknown",
                            vehicle = "Sesuai Kelas", // Fallback: API saat ini tidak mengirim detail kendaraan
                            bestTime = timeString,
                            gap = gapString,
                            status = mapStringToStatus(dto.status),
                            isFastestLap = dto.rank == 1 // Highlight untuk urutan pertama
                        )
                    }.sortedBy { it.position } // Pastikan data selalu urut berdasarkan rank

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            // Fallback jika UI tidak memiliki referensi nama acara dari halaman sebelumnya
                            eventName = "Live Leaderboard",
                            entries = mappedEntries
                        )
                    }
                }
                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    // --- FUNGSI HELPER ---

    /**
     * Mengubah milidetik (misal: 84350) menjadi format MM:SS.ms (01:24.350)
     */
    private fun formatTimeMs(timeMs: Long): String {
        val minutes = timeMs / 60000
        val seconds = (timeMs % 60000) / 1000
        val millis = timeMs % 1000

        // PadStart memastikan format tetap dua/tiga digit (misal: 05 detik, bukan 5 detik)
        val mStr = minutes.toString().padStart(2, '0')
        val sStr = seconds.toString().padStart(2, '0')
        val msStr = millis.toString().padStart(3, '0')

        return "$mStr:$sStr.$msStr"
    }

    /**
     * Menerjemahkan status string dari backend ke UI Enum
     */
    private fun mapStringToStatus(status: String?): RacerStatus {
        return when (status?.uppercase()) {
            "FINISHED", "COMPLETED" -> RacerStatus.FINISHED
            "DNF" -> RacerStatus.DNF
            "DSQ" -> RacerStatus.DSQ
            else -> RacerStatus.RACING
        }
    }
}