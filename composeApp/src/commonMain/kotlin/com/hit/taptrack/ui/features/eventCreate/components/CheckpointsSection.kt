package com.hit.taptrack.ui.features.eventCreate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateEvent
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateUiState

@Composable
fun CheckpointsSection(uiState: EventCreateUiState, onEvent: (EventCreateEvent) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header Utama
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Konfigurasi Rute",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Kelola Stage dan Checkpoint untuk setiap balapan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // LOOPING SPECIAL STAGE
            uiState.stages.forEachIndexed { stageIndex, stage ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    // Header per Stage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Special Stage ${stage.stageNumber}", fontWeight = FontWeight.Bold)

                        Row {
                            TextButton(onClick = { onEvent(EventCreateEvent.AddCheckpointToStage(stageIndex)) }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Pos Tengah", style = MaterialTheme.typography.labelSmall)
                            }
                            if (uiState.stages.size > 1) {
                                IconButton(onClick = { onEvent(EventCreateEvent.RemoveStage(stageIndex)) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus SS", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    HorizontalDivider(
                        Modifier.padding(vertical = 8.dp),
                        DividerDefaults.Thickness,
                        DividerDefaults.color
                    )

                    // LOOPING CHECKPOINTS DI DALAM STAGE
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        stage.checkpoints.forEachIndexed { cpIndex, checkpointName ->
                            val isFirst = cpIndex == 0
                            val isLast = cpIndex == stage.checkpoints.size - 1

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Ikon Tipe Pos
                                Box(
                                    modifier = Modifier.size(32.dp).background(
                                        if (isFirst || isLast) MaterialTheme.colorScheme.secondary else Color.Gray,
                                        CircleShape
                                    ), contentAlignment = Alignment.Center
                                ) {
                                    if (isFirst) Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    else if (isLast) Icon(Icons.Default.Flag, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    else Text("M", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                                Spacer(modifier = Modifier.width(12.dp))

                                // Input Nama
                                OutlinedTextField(
                                    value = checkpointName,
                                    onValueChange = { onEvent(EventCreateEvent.UpdateCheckpointName(stageIndex, cpIndex, it)) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.labelLarge
                                )

                                // Tombol Hapus (Hanya untuk pos tengah)
                                if (!isFirst && !isLast) {
                                    IconButton(onClick = { onEvent(EventCreateEvent.RemoveCheckpointFromStage(stageIndex, cpIndex)) }) {
                                        Icon(Icons.Default.Close, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(48.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Tombol Tambah Stage Baru di Paling Bawah
            OutlinedButton(
                onClick = { onEvent(EventCreateEvent.AddStage) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddLocationAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Stage Baru", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}