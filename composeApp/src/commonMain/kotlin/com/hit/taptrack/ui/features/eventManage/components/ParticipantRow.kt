package com.hit.taptrack.ui.features.eventManage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.common.state.ParticipantStatus
import com.hit.taptrack.data.model.ParticipantModel
import com.hit.taptrack.ui.features.eventManage.states.EventManageEvent


@Composable
fun ParticipantRow(participant: ParticipantModel, onEvent: (EventManageEvent) -> Unit) {
    val statusColor = when (participant.status) {
        ParticipantStatus.REGISTERED -> Color.Gray
        ParticipantStatus.RACING -> Color(0xFF2E7D32) // Hijau
        ParticipantStatus.FINISHED -> MaterialTheme.colorScheme.secondary
        ParticipantStatus.DNF, ParticipantStatus.DSQ -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable { onEvent(EventManageEvent.SelectParticipant(participant)) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurface)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    participant.racerNumber,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.surface
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    participant.racerName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    participant.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    participant.status.name,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }
        }
    }
}