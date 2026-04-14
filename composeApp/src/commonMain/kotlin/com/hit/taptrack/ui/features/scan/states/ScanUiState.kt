package com.hit.taptrack.ui.features.scan.states

import com.hit.taptrack.common.state.CheckpointType

data class ScanUiState(
    val eventId: String = "",
    val checkpoints: List<CheckpointUiModel> = emptyList(), // Daftar pos dinamis
    val selectedCheckpoint: CheckpointUiModel? = null,      // Pos yang dipilih (UUID asli)
    val manualInput: String = "",
    val isLoading: Boolean = false,
    val scanResult: ScanResult? = null,
    val pendingSyncCount: Int = 0,
    val errorMessage: String? = null
)