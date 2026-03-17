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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Konfigurasi Checkpoint",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = { onEvent(EventCreateEvent.AddCheckpoint("Pos Baru")) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Titik")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Render urutan Checkpoint (Visual Timeline)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.checkpoints.forEachIndexed { index, checkpointName ->
                    val isFirst = index == 0
                    val isLast = index == uiState.checkpoints.size - 1

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Ikon Urutan
                        Box(
                            modifier = Modifier.size(36.dp).background(
                                if (isFirst || isLast) MaterialTheme.colorScheme.secondary else Color.Gray.copy(
                                    alpha = 0.2f
                                ),
                                CircleShape
                            ), contentAlignment = Alignment.Center
                        ) {
                            if (isFirst || isLast) Icon(
                                if (isFirst) Icons.Default.Place else Icons.Default.Flag,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            else Text("${index}", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        // Input Nama Checkpoint
                        OutlinedTextField(
                            value = checkpointName,
                            onValueChange = {
                                onEvent(
                                    EventCreateEvent.UpdateCheckpoint(
                                        index,
                                        it
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !isFirst && !isLast // Start dan Finish tidak bisa diubah namanya di sini
                        )

                        // Tombol Hapus (Hanya muncul jika bukan Start dan Finish)
                        if (!isFirst && !isLast) {
                            IconButton(onClick = { onEvent(EventCreateEvent.RemoveCheckpoint(index)) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(48.dp)) // Menjaga spasi agar rata
                        }
                    }
                }
            }
        }
    }
}