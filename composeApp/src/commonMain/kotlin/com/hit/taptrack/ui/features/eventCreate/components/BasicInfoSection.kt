package com.hit.taptrack.ui.features.eventCreate.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BasicInfoSection(uiState: EventCreateUiState, onEvent: (EventCreateEvent) -> Unit) {
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
                label = { Text("Nama Acara") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.date,
                    onValueChange = { onEvent(EventCreateEvent.DateChanged(it)) },
                    label = { Text("Tanggal (Mis: 15 Apr 2026)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.maxCapacity,
                    onValueChange = { onEvent(EventCreateEvent.MaxCapacityChanged(it)) },
                    label = { Text("Kapasitas") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            OutlinedTextField(
                value = uiState.location,
                onValueChange = { onEvent(EventCreateEvent.LocationChanged(it)) },
                label = { Text("Lokasi Sirkuit") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { onEvent(EventCreateEvent.DescriptionChanged(it)) },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4
            )
            OutlinedTextField(
                value = uiState.rules,
                onValueChange = { onEvent(EventCreateEvent.RulesChanged(it)) },
                label = { Text("Regulasi Khusus") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4
            )
        }
    }
}