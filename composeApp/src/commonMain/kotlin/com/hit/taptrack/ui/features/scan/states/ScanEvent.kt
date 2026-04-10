package com.hit.taptrack.ui.features.scan.states

import com.hit.taptrack.common.state.CheckpointType

sealed class ScanEvent {
    data class CheckpointSelected(val checkpoint: CheckpointType) : ScanEvent()
    data class ManualInputChanged(val input: String) : ScanEvent()
    data class ProcessScan(val payload: String) : ScanEvent() // Dipanggil saat kamera berhasil baca QR
    object SubmitManualScan : ScanEvent() // Dipanggil saat panitia ketik manual dan tekan tombol
    object DismissResult : ScanEvent() // Menutup popup/banner hasil pindai
    object SyncPendingScans : ScanEvent()
}