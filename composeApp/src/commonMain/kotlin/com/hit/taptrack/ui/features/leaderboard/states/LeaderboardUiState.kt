package com.hit.taptrack.ui.features.leaderboard.states

import com.hit.taptrack.data.model.LeaderboardEntryModel

data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val eventName: String = "",
    val currentCategory: String = "FFA (Free For All)",
    val categories: List<String> = listOf(
        "Bebek Modif 4T 130cc",
        "Sport Trail 250cc Open",
        "FFA (Free For All)"
    ),
    val entries: List<LeaderboardEntryModel> = emptyList(),
    val errorMessage: String? = null
)