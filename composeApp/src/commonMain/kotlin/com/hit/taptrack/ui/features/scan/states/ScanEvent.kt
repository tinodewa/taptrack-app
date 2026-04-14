package com.hit.taptrack.ui.features.scan.states

sealed class ScanEvent {
    // Ubah parameternya menjadi model dinamis
    data class CheckpointSelected(val checkpoint: CheckpointUiModel) : ScanEvent()
    data class ManualInputChanged(val input: String) : ScanEvent()
    data class ProcessScan(val payload: String) : ScanEvent()
    object SubmitManualScan : ScanEvent()
    object DismissResult : ScanEvent()
    object SyncPendingScans : ScanEvent()
}