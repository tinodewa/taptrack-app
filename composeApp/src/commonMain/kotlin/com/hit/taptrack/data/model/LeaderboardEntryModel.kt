package com.hit.taptrack.data.model

import com.hit.taptrack.common.state.RacerStatus

data class LeaderboardEntryModel(
    val position: Int,
    val racerNumber: String,
    val racerName: String,
    val vehicle: String,
    val bestTime: String,
    val gap: String, // Selisih waktu dengan pemimpin (misal: "+1.245s")
    val status: RacerStatus,
    val isFastestLap: Boolean = false // Jika true, akan kita beri warna khusus (ungu)
)