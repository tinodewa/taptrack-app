package com.hit.taptrack.ui.features.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.scan.components.CheckpointSelector
import com.hit.taptrack.ui.features.scan.states.ScanEvent
import com.hit.taptrack.ui.features.scan.states.ScanUiState

// --- TAMPILAN MOBILE ---
@Composable
fun ScanCompact(uiState: ScanUiState, onEvent: (ScanEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {

        // Pemilihan Checkpoint (Sangat Krusial)
        Text("Pilih Lokasi Pindai:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        CheckpointSelector(selected = uiState.selectedCheckpoint, onSelect = { onEvent(ScanEvent.CheckpointSelected(it)) })

        Spacer(modifier = Modifier.height(32.dp))

        // Placeholder Kamera
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(Color.Black), contentAlignment = Alignment.Center) {
            // TODO: Ganti Box ini dengan KMP Camera / Scanner Library
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Kamera Aktif", color = Color.White.copy(alpha = 0.5f))

                // Tombol Flashlight (Simulasi)
                IconButton(onClick = { /* Toggle Flash */ }, modifier = Modifier.padding(top = 16.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))) {
                    Icon(Icons.Default.FlashlightOn, contentDescription = "Senter", tint = Color.White)
                }
            }

            // Jendela Bidik (Scanner Frame)
            Box(modifier = Modifier.fillMaxSize(0.7f).border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)))

            // Garis Laser (Simulasi)
            Box(modifier = Modifier.fillMaxWidth(0.6f).height(2.dp).background(MaterialTheme.colorScheme.error))

            // Tombol Simulasi Scan Berhasil (UNTUK TESTING)
            Button(
                onClick = { onEvent(ScanEvent.ProcessScan("TAPTRACK-REG-9912A")) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            ) { Text("Simulasi Scan QR") }
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
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
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
    Row(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(24.dp)).background(Color.Black), contentAlignment = Alignment.Center) {
            // Sama seperti placeholder kamera di atas
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                Text("Kamera Desktop / Scanner USB Aktif", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(top = 16.dp))
            }
            Box(modifier = Modifier.fillMaxSize(0.6f).border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)))
            Button(onClick = { onEvent(ScanEvent.ProcessScan("TAPTRACK-REG-9912A")) }, modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)) { Text("Simulasi Scan QR") }
        }

        Column(modifier = Modifier.weight(0.8f).fillMaxHeight()) {
            Text("Pengaturan Pindai", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Text("Lokasi Checkpoint:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            CheckpointSelector(selected = uiState.selectedCheckpoint, onSelect = { onEvent(ScanEvent.CheckpointSelected(it)) })

            Spacer(modifier = Modifier.height(48.dp))
            Text("Input Manual (Fallback)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = uiState.manualInput, onValueChange = { onEvent(ScanEvent.ManualInputChanged(it)) }, placeholder = { Text("Nomor Start / ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onEvent(ScanEvent.SubmitManualScan) }, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = !uiState.isLoading && uiState.manualInput.isNotBlank()) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary) else Text("KIRIM LOG MANUAL", fontWeight = FontWeight.Bold)
            }
        }
    }
}