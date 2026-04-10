package com.hit.taptrack.ui.features.scan

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.data.sources.local.dao.ScanLogDao
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
    private val tapTrackRepository: TapTrackRepository,
    private val scanLogDao: ScanLogDao
) : ScreenModel {

    private val _uiState = MutableStateFlow(ScanUiState(eventId = eventId))
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    init {
        // Observasi antrean tertunda secara real-time!
        screenModelScope.launch {
            scanLogDao.getPendingCountFlow(eventId).collect { count ->
                _uiState.update { it.copy(pendingSyncCount = count) }
            }
        }
    }

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
            ScanEvent.SyncPendingScans -> performSync()
        }
    }

    private fun executeScan(payload: String, isManual: Boolean) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, scanResult = null) }

            val currentMoment = Clock.System.now()
            val absoluteTimestamp = currentMoment.toString()
            val localTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
            val displayTime = "${localTime.hour.toString().padStart(2, '0')}:${
                localTime.minute.toString().padStart(2, '0')
            }:${localTime.second.toString().padStart(2, '0')}"

            val request = ScanQrRequest(
                qrTokenHash = payload,
                checkpointId = _uiState.value.selectedCheckpoint.name,
                absoluteTimestamp = absoluteTimestamp,
                manualOverrideFlag = isManual.toString()
            )

            // GANTI PEMANGGILAN INI KE VERSI OFFLINE-FIRST
            val result = tapTrackRepository.scanQrOfflineFirst(eventId, request)

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

    private fun performSync() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = tapTrackRepository.syncPendingScans()

            when (result) {
                is ResultState.Success -> {
                    val count = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            scanResult = ScanResult.Success("Berhasil sinkronisasi $count data.", "")
                        )
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}