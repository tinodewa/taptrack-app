package com.hit.taptrack.ui.features.eventDiscovery.states

import com.hit.taptrack.data.model.EventModel

data class EventDiscoveryUiState(
    val allEvents: List<EventModel> = emptyList(),
    val filteredEvents: List<EventModel> = emptyList(),
    val selectedTabIndex: Int = 0, // 0: Mendatang, 1: Berlangsung, 2: Selesai
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isCommittee: Boolean = false,
    val errorMessage: String? = null
)