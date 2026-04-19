package com.hit.taptrack.ui.features.eventCreate.states

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun BasicInfoSection(uiState: EventCreateUiState, onEvent: (EventCreateEvent) -> Unit) {
    // State untuk memunculkan/menyembunyikan kalender
    var showDatePicker by remember { mutableStateOf(false) }
    // State bawaan Material 3 untuk kalender
    val datePickerState = rememberDatePickerState()

    // Fungsi bantu untuk memformat format ISO ("2026-04-19T00:00:00Z") menjadi ramah baca ("19 APRIL 2026")
    val displayDate = remember(uiState.date) {
        if (uiState.date.isNotBlank()) {
            try {
                val instant = Instant.parse(uiState.date)
                val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                "${localDate.dayOfMonth} ${localDate.month.name} ${localDate.year}"
            } catch (e: Exception) {
                uiState.date // Fallback jika gagal parse
            }
        } else {
            ""
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Informasi Dasar",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { onEvent(EventCreateEvent.NameChanged(it)) },
                label = { Text("Nama Acara", style = MaterialTheme.typography.labelLarge) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.labelLarge,
            )
            // TEXTFIELD TANGGAL YANG SUDAH DIPERBARUI
            Box(modifier = Modifier.fillMaxWidth(),) {
                OutlinedTextField(
                    value = displayDate,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Tanggal", style = MaterialTheme.typography.labelLarge) },
                    placeholder = { Text("Pilih di Kalender") },
                    trailingIcon = {
                        // Hapus IconButton, cukup gunakan Icon saja karena seluruh area sudah bisa diklik
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    },
                    modifier = Modifier.fillMaxWidth(), // Isi penuh area Box
                    textStyle = MaterialTheme.typography.labelLarge,
                )

                // Lapisan transparan (overlay) yang menutupi seluruh OutlinedTextField
                Box(
                    modifier = Modifier
                        .matchParentSize() // Menyamakan ukuran persis dengan OutlinedTextField
                        .background(Color.Transparent)
                        .clickable { showDatePicker = true } // Pemicu popup kalender
                )
            }
            OutlinedTextField(
                value = uiState.maxCapacity,
                onValueChange = { onEvent(EventCreateEvent.MaxCapacityChanged(it)) },
                label = { Text("Kapasitas", style = MaterialTheme.typography.labelLarge) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.labelLarge,
            )
            OutlinedTextField(
                value = uiState.location,
                onValueChange = { onEvent(EventCreateEvent.LocationChanged(it)) },
                label = { Text("Lokasi Sirkuit", style = MaterialTheme.typography.labelLarge) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.labelLarge,
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { onEvent(EventCreateEvent.DescriptionChanged(it)) },
                label = { Text("Deskripsi", style = MaterialTheme.typography.labelLarge) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4,
                textStyle = MaterialTheme.typography.labelLarge,
            )
            OutlinedTextField(
                value = uiState.rules,
                onValueChange = { onEvent(EventCreateEvent.RulesChanged(it)) },
                label = { Text("Regulasi Khusus", style = MaterialTheme.typography.labelLarge) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4,
                textStyle = MaterialTheme.typography.labelLarge,
            )
        }
    }

    // ==========================================
    // DIALOG DATE PICKER (MATERIAL 3)
    // ==========================================
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Konversi mili-detik (epoch) dari kalender menjadi format ISO8601 (RFC3339)
                        // Hasilnya persis seperti yang diminta Golang: "2026-04-19T00:00:00Z"
                        val isoString = Instant.fromEpochMilliseconds(millis).toString()
                        onEvent(EventCreateEvent.DateChanged(isoString))
                    }
                    showDatePicker = false
                }) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}