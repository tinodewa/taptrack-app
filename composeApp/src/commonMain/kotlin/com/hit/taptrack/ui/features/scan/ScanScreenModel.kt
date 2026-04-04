package com.hit.taptrack.ui.features.scan

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.data.sources.remote.requestBody.ScanQrRequest
import com.hit.taptrack.ui.features.scan.states.ScanEvent
import com.hit.taptrack.ui.features.scan.states.ScanResult
import com.hit.taptrack.ui.features.scan.states.ScanUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class ScanScreenModel(
    private val eventId: String,
    private val tapTrackRepository: TapTrackRepository
     ) : ScreenModel {

    private val _uiState = MutableStateFlow(ScanUiState(eventId = eventId))
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun onEvent(event: ScanEvent) {
        when (event) {
            is ScanEvent.CheckpointSelected -> _uiState.update { it.copy(selectedCheckpoint = event.checkpoint) }
            is ScanEvent.ManualInputChanged -> _uiState.update { it.copy(manualInput = event.input) }
            is ScanEvent.ProcessScan -> executeScan(event.payload, isManual = false) // Dari Kamera
            ScanEvent.SubmitManualScan -> {
                val input = _uiState.value.manualInput
                if (input.isNotBlank()) {
                    executeScan(input, isManual = true) // Dari Input Teks
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

    private fun executeScan(payload: String, isManual: Boolean) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, scanResult = null) }

            // 1. Dapatkan waktu saat ini
            val currentMoment = Clock.System.now()
            val absoluteTimestamp = currentMoment.toString() // Format ISO-8601 (Cth: 2026-04-12T08:30:00Z)

            // Format waktu untuk ditampilkan di UI (Cth: 08:30:05)
            val localTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
            val displayTime = "${localTime.hour.toString().padStart(2, '0')}:${localTime.minute.toString().padStart(2, '0')}:${localTime.second.toString().padStart(2, '0')}"

            // 2. Siapkan Request
            val request = ScanQrRequest(
                qrTokenHash = payload,
                checkpointId = _uiState.value.selectedCheckpoint.name, // Cth: "START", "CHECKPOINT_1"
                absoluteTimestamp = absoluteTimestamp,
                manualOverrideFlag = isManual.toString() // "true" atau "false"
            )

            // 3. Tembak API
            val result = tapTrackRepository.scanQr(request)

            // 4. Tangani Hasil
            when (result) {
                is ResultState.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            scanResult = ScanResult.Success(
                                message = result.data.message ?: "Log Tersimpan",
                                time = displayTime
                            )
                        )
                    }
                }
                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            scanResult = ScanResult.Error(result.message)
                        )
                    }
                }
            }
        }
    }
}