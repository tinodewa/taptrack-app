package com.hit.taptrack.ui.features.eventManage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hit.taptrack.common.state.ParticipantStatus
import com.hit.taptrack.data.model.ParticipantModel
import com.hit.taptrack.ui.features.eventManage.states.EventManageEvent

@Composable
fun OverrideParticipantDialog(
    participant: ParticipantModel,
    isLoading: Boolean,
    onEvent: (EventManageEvent) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Override Status Peserta",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Info Peserta
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.onSurface)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "#${participant.racerNumber}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = participant.racerName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = participant.registrationId,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Pilih Status Baru:", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Grid Tombol Aksi Override
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Tombol DSQ (Diskualifikasi) - Paling Fatal
                    OverrideButton(
                        text = "DSQ (Disqualified)",
                        color = Color.Black,
                        isLoading = isLoading,
                        onClick = {
                            onEvent(
                                EventManageEvent.OverrideParticipantStatus(
                                    participant.registrationId,
                                    ParticipantStatus.DSQ
                                )
                            )
                        }
                    )

                    // Tombol DNF (Tidak Finis) - Kecelakaan/Mogok
                    OverrideButton(
                        text = "DNF (Did Not Finish)",
                        color = MaterialTheme.colorScheme.error,
                        isLoading = isLoading,
                        onClick = {
                            onEvent(
                                EventManageEvent.OverrideParticipantStatus(
                                    participant.registrationId,
                                    ParticipantStatus.DNF
                                )
                            )
                        }
                    )

                    // Tombol Reset ke Racing (Jika salah pencet)
                    OverrideButton(
                        text = "KEMBALIKAN KE LINTASAN (RACING)",
                        color = Color(0xFF2E7D32), // Hijau
                        isLoading = isLoading,
                        onClick = {
                            onEvent(
                                EventManageEvent.OverrideParticipantStatus(
                                    participant.registrationId,
                                    ParticipantStatus.RACING
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = onDismiss, enabled = !isLoading) {
                    Text("Batal", color = Color.Gray)
                }
            }
        }
    }
}