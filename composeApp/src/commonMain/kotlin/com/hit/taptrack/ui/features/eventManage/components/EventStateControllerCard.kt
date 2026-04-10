package com.hit.taptrack.ui.features.eventManage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Mulai (Hanya aktif jika Upcoming)
                Button(
                    onClick = { onEvent(EventManageEvent.ChangeEventStatus(EventStatus.ACTIVE)) },
                    enabled = !isChanging && currentStatus == EventStatus.UPCOMING,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MULAI (LIVE)")
                }

                // Tombol Selesai (Hanya aktif jika Ongoing)
                Button(
                    onClick = { onEvent(EventManageEvent.ChangeEventStatus(EventStatus.FINISHED)) },
                    enabled = !isChanging && currentStatus == EventStatus.ACTIVE,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SELESAIKAN")
                }
            }
            if (currentStatus == EventStatus.FINISHED) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Acara telah diselesaikan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}