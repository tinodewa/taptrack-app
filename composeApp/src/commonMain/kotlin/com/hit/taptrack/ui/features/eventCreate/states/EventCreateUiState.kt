package com.hit.taptrack.ui.features.eventCreate.states

import com.hit.taptrack.data.model.StageInputModel

data class EventCreateUiState(
    val name: String = "",
    val date: String = "",
    val location: String = "",
    val description: String = "",
    val rules: String = "",
    val maxCapacity: String = "",
    val categories: List<String> = listOf("Hobby", "Pro"), // Sesuai JSON baru
    // Default 1 Special Stage saat halaman dibuka
    val stages: List<StageInputModel> = listOf(
        StageInputModel(stageNumber = 1, checkpoints = listOf("SS1 Start", "SS1 Finish"))
    ),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)