package com.hit.taptrack.ui.features.scan

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.ui.features.scan.states.ScanEvent
import com.hit.taptrack.ui.features.scan.states.ScanResult
import com.hit.taptrack.ui.features.scan.states.ScanUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScanScreenModel(private val eventId: String) : ScreenModel {

    private val _uiState = MutableStateFlow(ScanUiState(eventId = eventId))
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun onEvent(event: ScanEvent) {
        when (event) {
            is ScanEvent.CheckpointSelected -> _uiState.update { it.copy(selectedCheckpoint = event.checkpoint) }
            is ScanEvent.ManualInputChanged -> _uiState.update { it.copy(manualInput = event.input) }
            is ScanEvent.ProcessScan -> executeScan(event.payload)
            ScanEvent.SubmitManualScan -> {
                val input = _uiState.value.manualInput
                if (input.isNotBlank()) {
                    executeScan(input) // Anggap input manual adalah ID/Payload
                }
            }

            ScanEvent.DismissResult -> _uiState.update {
                it.copy(
                    scanResult = null,
                    manualInput = ""
                )
            }
        }
    }

    private fun executeScan(payload: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, scanResult = null) }

            // TODO: Tembak endpoint POST /api/v1/scans
            // Body: { "event_id": eventId, "payload": payload, "checkpoint": selectedCheckpoint.name, "timestamp": <waktu_sekarang> }

            delay(800) // Simulasi network & proses validasi

            // Simulasi Logika: Jika payload mengandung kata "ERROR", kita anggap gagal.
            if (payload.contains("ERROR", ignoreCase = true)) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        scanResult = ScanResult.Error("QR Code tidak valid atau peserta tidak terdaftar.")
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        scanResult = ScanResult.Success(
                            racerName = "Andi Gilang",
                            racerNumber = "99",
                            time = "14:32:05 WIB"
                        )
                    )
                }
            }
        }
    }
}