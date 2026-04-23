package com.hit.taptrack.ui.features.eventDetail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.common.state.UserRole
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailEvent
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailUiState

// Komponen Logika Tombol berdasarkan Role
@Composable
fun DynamicActionButton(
    uiState: EventDetailUiState,
    onEvent: (EventDetailEvent) -> Unit,
    onNavigateToManageEvent: () -> Unit
) {
    val isUpcoming = uiState.eventDetail?.status == EventStatus.UPCOMING

    when (uiState.userRole) {
        UserRole.RACER -> {
            Button(
                // Logika Klik: Jika sudah sukses daftar, pergi ke tiket. Jika belum, lakukan JoinEvent.
                onClick = {
                    if (uiState.isJoinSuccess || uiState.isRegistered) onEvent(EventDetailEvent.ShowTicketDialog)
                    else onEvent(EventDetailEvent.ShowJoinDialog)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                // Tombol aktif jika loading selesai, event upcoming, ATAU jika sudah sukses daftar
                enabled = !uiState.isJoinLoading && (isUpcoming || uiState.isJoinSuccess || uiState.isRegistered),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState.isJoinLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        // Ubah teks secara dinamis
                        text = if (uiState.isJoinSuccess || uiState.isRegistered) "LIHAT TIKET"
                        else if (isUpcoming) "GABUNG BALAPAN"
                        else "PENDAFTARAN DITUTUP",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        UserRole.COMMITTEE -> {
            Button(
                onClick = onNavigateToManageEvent,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    "KELOLA ACARA",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}