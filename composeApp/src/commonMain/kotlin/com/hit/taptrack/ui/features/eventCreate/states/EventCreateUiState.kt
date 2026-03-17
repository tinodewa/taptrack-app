package com.hit.taptrack.ui.features.eventCreate.states

data class EventCreateUiState(
    val name: String = "",
    val date: String = "",
    val location: String = "",
    val description: String = "",
    val rules: String = "",
    val maxCapacity: String = "",
    val categories: List<String> = listOf("FFA (Free For All)"), // Default 1 kelas
    val checkpoints: List<String> = listOf("Garis Start", "Garis Finish"), // Wajib ada 2
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)