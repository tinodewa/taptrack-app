package com.hit.taptrack.ui.features.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.scan.components.CheckpointSelector
import com.hit.taptrack.ui.features.scan.components.QRScannerView
import com.hit.taptrack.ui.features.scan.components.RequireCameraPermission
import com.hit.taptrack.ui.features.scan.states.ScanEvent
import com.hit.taptrack.ui.features.scan.states.ScanUiState

// --- TAMPILAN MOBILE ---
@Composable
fun ScanCompact(uiState: ScanUiState, onEvent: (ScanEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {

        // Pemilihan Checkpoint (Sangat Krusial)
        Text(
            "Pilih Lokasi Pindai:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        CheckpointSelector(
            checkpoints = uiState.checkpoints,
            selected = uiState.selectedCheckpoint,
            onSelect = { onEvent(ScanEvent.CheckpointSelected(it)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            RequireCameraPermission(
                onPermissionGranted = {
                    // ==========================================
                    // KODE KAMERA ASLI (Yang di dalam Box hitam)
                    // ==========================================
                    Box(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        // Panggil QRScannerView dan garis laser di sini
                        QRScannerView(
                            modifier = Modifier.fillMaxSize(),
                            onScan = { payload ->
                                // NOTE: The camera reads very fast. Ensure your ScreenModel
                                // ignores duplicate payload requests if `isLoading` is true.
                                onEvent(ScanEvent.ProcessScan(payload))
                            }
                        )

                        // 2. Your Custom Viewfinder (Scanner Frame)
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.7f)
                                .border(
                                    4.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(16.dp)
                                )
                        )

                        // 3. Your Custom Laser Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(2.dp)
                                .background(MaterialTheme.colorScheme.error)
                        )

                        // Optional: Keep the simulation button around for easy emulator testing
                        Button(
                            onClick = { onEvent(ScanEvent.ProcessScan("TAPTRACK-REG-9912A")) },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) {
                            Text("Simulate QR")
                        }
                    }
                },
                onPermissionDenied = { requestPermission ->
                    // ==========================================
                    // TAMPILAN JIKA IZIN DITOLAK
                    // ==========================================
                    Column(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoPhotography,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Akses Kamera Dibutuhkan",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Kami memerlukan kamera untuk memindai tiket peserta.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = requestPermission) {
                            Text("Berikan Izin")
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Fallback: Input Manual
        Text("Atau Masukkan Nomor Lambung / ID:", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = uiState.manualInput,
                onValueChange = { onEvent(ScanEvent.ManualInputChanged(it)) },
                placeholder = { Text("Cth: 99 atau REG-123") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { onEvent(ScanEvent.SubmitManualScan) },
                modifier = Modifier.height(56.dp),
                enabled = !uiState.isLoading && uiState.manualInput.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("KIRIM")
                }
            }
        }
    }
}

// --- TAMPILAN DESKTOP (Split View) ---
@Composable
fun ScanExpanded(uiState: ScanUiState, onEvent: (ScanEvent) -> Unit) {
    // Implementasi serupa dengan Compact, namun dibagi Row.
    // Kiri: Kamera Scanner. Kanan: Kontrol (Pilih Checkpoint & Input Manual).
    Row(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(24.dp))
                .background(Color.Black), contentAlignment = Alignment.Center
        ) {
            // Sama seperti placeholder kamera di atas
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    "Kamera Desktop / Scanner USB Aktif",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Box(
                modifier = Modifier.fillMaxSize(0.6f)
                    .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
            )
            Button(
                onClick = { onEvent(ScanEvent.ProcessScan("TAPTRACK-REG-9912A")) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)
            ) { Text("Simulasi Scan QR") }
        }

        Column(modifier = Modifier.weight(0.8f).fillMaxHeight()) {
            Text("Pengaturan Pindai", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Text("Lokasi Checkpoint:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            CheckpointSelector(
                checkpoints = uiState.checkpoints,
                selected = uiState.selectedCheckpoint,
                onSelect = { onEvent(ScanEvent.CheckpointSelected(it)) }
            )

            Spacer(modifier = Modifier.height(48.dp))
            Text("Input Manual (Fallback)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.manualInput,
                onValueChange = { onEvent(ScanEvent.ManualInputChanged(it)) },
                placeholder = { Text("Nomor Start / ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onEvent(ScanEvent.SubmitManualScan) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isLoading && uiState.manualInput.isNotBlank()
            ) {
                if (uiState.isLoading) CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                ) else Text("KIRIM LOG MANUAL", fontWeight = FontWeight.Bold)
            }
        }
    }
}