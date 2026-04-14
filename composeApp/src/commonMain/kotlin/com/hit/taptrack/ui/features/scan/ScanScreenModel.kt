package com.hit.taptrack.ui.features.scan

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.data.sources.local.dao.ScanLogDao
import com.hit.taptrack.data.sources.remote.requestBody.ScanQrRequest
import com.hit.taptrack.ui.features.scan.states.CheckpointUiModel
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

    // variabel untuk delay scan agar tidak spam
    private var lastScannedPayload: String? = null
    private var lastScanTime: Long = 0L
    private val COOLDOWN_DURATION_MS = 2500L // Waktu jeda 2.5 detik untuk QR yang sama

    init {
        // Observasi antrean tertunda
        screenModelScope.launch {
            scanLogDao.getPendingCountFlow(eventId).collect { count ->
                _uiState.update { it.copy(pendingSyncCount = count) }
            }
        }

        // Ambil dan Sinkronisasi Daftar Pos (Checkpoint)
        loadCheckpoints()
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

    private fun loadCheckpoints() {
        screenModelScope.launch {
            // 1. Ambil dari API untuk jaga-jaga ada perubahan (berjalan di background)
            tapTrackRepository.syncCheckpoints(eventId)

            // 2. Baca terus-menerus dari SQLite (Room)
            tapTrackRepository.getCheckpointsFlow(eventId).collect { entities ->
                val mapped = entities.map {
                    CheckpointUiModel(it.id, it.checkpointName, it.typeFlag)
                }

                _uiState.update { state ->
                    // Auto-pilih pos pertama jika sebelumnya belum ada yang terpilih
                    val currentSelected = state.selectedCheckpoint
                    val newSelected =
                        if (currentSelected == null && mapped.isNotEmpty()) mapped.first() else currentSelected

                    state.copy(checkpoints = mapped, selectedCheckpoint = newSelected)
                }
            }
        }
    }

    private fun executeScan(payload: String, isManual: Boolean) {
        // ==========================================
        // 1. KUNCI PERTAMA: Sedang Loading
        // ==========================================
        if (_uiState.value.isLoading) return

        // ==========================================
        // 2. KUNCI KEDUA: Cooldown QR yang Sama
        // ==========================================
        val currentMillis = Clock.System.now().toEpochMilliseconds()

        if (!isManual) {
            val isSamePayload = payload == lastScannedPayload
            val isWithinCooldown = (currentMillis - lastScanTime) < COOLDOWN_DURATION_MS

            // Jika QR sama persis dan masih dalam masa jeda, abaikan!
            if (isSamePayload && isWithinCooldown) {
                return
            }

            // Jika lolos, perbarui memori pemindaian terakhir
            lastScannedPayload = payload
            lastScanTime = currentMillis
        }

        // ==========================================
        // PROSES UTAMA (Aman dari Spam)
        // ==========================================
        screenModelScope.launch {

            // Cegah scan jika belum ada pos yang dipilih/di-load
            val currentCheckpoint = _uiState.value.selectedCheckpoint ?: return@launch

            _uiState.update { it.copy(isLoading = true, scanResult = null) }

            val currentMoment = Clock.System.now()
            val absoluteTimestamp = currentMoment.toString()
            val localTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
            val displayTime = "${localTime.hour.toString().padStart(2, '0')}:${
                localTime.minute.toString().padStart(2, '0')
            }:${localTime.second.toString().padStart(2, '0')}"

            val request = ScanQrRequest(
                qrTokenHash = payload,
                checkpointId = currentCheckpoint.id,
                absoluteTimestamp = absoluteTimestamp,
                manualOverrideFlag = isManual
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
                    // Jika error (misal QR tidak valid), kita reset memori
                    // agar panitia bisa mencoba scan QR itu lagi tanpa harus menunggu 2.5 detik
                    lastScannedPayload = null

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
                            scanResult = ScanResult.Success(
                                "Berhasil sinkronisasi $count data.",
                                ""
                            )
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