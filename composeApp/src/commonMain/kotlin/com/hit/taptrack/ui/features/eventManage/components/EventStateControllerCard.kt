package com.hit.taptrack.ui.features.eventManage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.ui.features.eventManage.states.EventManageEvent

@Composable
fun EventStateControllerCard(
    currentStatus: EventStatus,
    isChanging: Boolean,
    onEvent: (EventManageEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Status Balapan", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                // Kurangi jarak antar tombol menjadi 8.dp agar muat 3 tombol
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Tombol Reset (Kembali ke Upcoming)
                // Bersifat korektif, kita beri warna netral/outline gelap
                Button(
                    onClick = { onEvent(EventManageEvent.ChangeEventStatus(EventStatus.UPCOMING)) },
                    // Aktif jika acara sedang berjalan atau sudah selesai
                    enabled = !isChanging && currentStatus != EventStatus.UPCOMING,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp) // Kurangi padding dalam
                ) {
                    Icon(Icons.Default.Restore, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("RESET", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                // 2. Tombol Mulai (Maju ke Active)
                Button(
                    onClick = { onEvent(EventManageEvent.ChangeEventStatus(EventStatus.ACTIVE)) },
                    enabled = !isChanging && currentStatus == EventStatus.UPCOMING,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Mulai")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("MULAI", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                // 3. Tombol Selesai (Maju ke Finished)
                Button(
                    onClick = { onEvent(EventManageEvent.ChangeEventStatus(EventStatus.FINISHED)) },
                    enabled = !isChanging && currentStatus == EventStatus.ACTIVE,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Selesai")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SELESAI", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teks panduan dinamis di bawah tombol
            val statusMessage = when(currentStatus) {
                EventStatus.UPCOMING -> "Acara belum dimulai. Tekan MULAI untuk mengaktifkan pemindai."
                EventStatus.ACTIVE -> "Acara sedang LIVE. Jika tidak sengaja termulai, Anda bisa me-Reset."
                EventStatus.FINISHED -> "Acara telah diselesaikan. Hasil akhir sudah terkunci."
                else -> ""
            }

            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (currentStatus == EventStatus.FINISHED) MaterialTheme.colorScheme.error else Color.Gray
            )
        }
    }
}