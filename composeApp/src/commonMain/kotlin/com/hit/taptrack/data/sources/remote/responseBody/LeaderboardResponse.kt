package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LeaderboardResponse(
    @SerialName("event_id") var eventId: String? = null,
    @SerialName("leaderboard") var leaderboard: ArrayList<Leaderboard> = arrayListOf()
)

@Serializable
data class Leaderboard(
    @SerialName("rank") var rank: Int? = null,
    @SerialName("race_number") var raceNumber: String? = null,
    @SerialName("racer_name") var racerName: String? = null,
    @SerialName("elapsed_time_ms") var elapsedTimeMs: Long? = null,
    @SerialName("status") var status: String? = null
)