package com.hit.taptrack.ui.features.eventManage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.eventManage.components.EventStateControllerCard
import com.hit.taptrack.ui.features.eventManage.components.ParticipantRow
import com.hit.taptrack.ui.features.eventManage.components.ScannerActionCard
import com.hit.taptrack.ui.features.eventManage.states.EventManageEvent
import com.hit.taptrack.ui.features.eventManage.states.EventManageUiState

// --- TAMPILAN MOBILE ---
@Composable
fun EventManageCompact(
    uiState: EventManageUiState,
    onEvent: (EventManageEvent) -> Unit,
    onNavigateToScanner: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            EventStateControllerCard(uiState.eventStatus, uiState.isStatusChanging, onEvent)
        }
        item {
            ScannerActionCard(onNavigateToScanner)
        }
        item {
            Text(
                "Daftar Peserta (${uiState.participants.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }
        items(uiState.participants) { participant ->
            ParticipantRow(participant)
        }
    }
}

// --- TAMPILAN DESKTOP ---
@Composable
fun EventManageExpanded(
    uiState: EventManageUiState,
    onEvent: (EventManageEvent) -> Unit,
    onNavigateToScanner: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Kiri: Kontrol Utama
        Column(modifier = Modifier.weight(0.8f)) {
            EventStateControllerCard(uiState.eventStatus, uiState.isStatusChanging, onEvent)
            Spacer(modifier = Modifier.height(24.dp))
            ScannerActionCard(onNavigateToScanner)
        }

        // Kanan: List Peserta
        Card(
            modifier = Modifier.weight(1.2f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Daftar Peserta (${uiState.participants.size})",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.participants) { participant ->
                        ParticipantRow(participant)
                    }
                }
            }
        }
    }
}