package com.hit.taptrack.ui.features.leaderboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hit.taptrack.common.state.RacerStatus
import com.hit.taptrack.data.model.LeaderboardEntryModel

@Composable
fun LeaderboardRow(entry: LeaderboardEntryModel) {
    // Penentuan Warna Khusus
    val isPurpleSector = entry.isFastestLap
    val cardColor =
        if (entry.position == 1) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val highlightColor =
        if (isPurpleSector) Color(0xFF9C27B0) else if (entry.position == 1) MaterialTheme.colorScheme.secondary else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kolom 1: Posisi (Highlight kiri)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(highlightColor)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = entry.position.toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Kolom 2: Nomor & Nama
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Kotak Nomor Start
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onSurface)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = entry.racerNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.racerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = entry.vehicle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Kolom 3: Waktu / Gap & Status (Menggunakan Monospace agar presisi)
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(100.dp)) {
                when (entry.status) {
                    RacerStatus.DNF -> Text(
                        "DNF",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.error
                    )

                    RacerStatus.DSQ -> Text(
                        "DSQ",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.Black
                    )

                    else -> {
                        Text(
                            text = entry.bestTime,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isPurpleSector) Color(0xFF9C27B0) else MaterialTheme.colorScheme.onSurface
                        )
                        if (entry.position > 1) {
                            Text(
                                text = entry.gap,
                                style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                "LEADER",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}