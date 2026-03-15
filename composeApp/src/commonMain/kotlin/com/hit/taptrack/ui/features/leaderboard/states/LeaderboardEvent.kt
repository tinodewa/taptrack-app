package com.hit.taptrack.ui.features.leaderboard.states

sealed class LeaderboardEvent {
    object RefreshLeaderboard : LeaderboardEvent()
    data class ChangeCategory(val newCategory: String) : LeaderboardEvent()
}