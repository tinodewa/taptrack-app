package com.hit.taptrack.ui.features.myTicket

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.myTicket.components.QrCodeDetailPanel
import com.hit.taptrack.ui.features.myTicket.components.TicketCard
import com.hit.taptrack.ui.features.myTicket.states.MyTicketsEvent
import com.hit.taptrack.ui.features.myTicket.states.MyTicketsUiState

// --- TAMPILAN MOBILE ---
@Composable
fun MyTicketsCompact(uiState: MyTicketsUiState, onEvent: (MyTicketsEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(uiState.tickets) { ticket ->
            TicketCard(ticket = ticket, onClick = { onEvent(MyTicketsEvent.SelectTicket(ticket)) })
        }
    }
}

// --- TAMPILAN DESKTOP (Split View) ---
@Composable
fun MyTicketsExpanded(uiState: MyTicketsUiState, onEvent: (MyTicketsEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Kiri: Daftar Tiket
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.tickets) { ticket ->
                    TicketCard(
                        ticket = ticket,
                        onClick = { onEvent(MyTicketsEvent.SelectTicket(ticket)) })
                }
            }
        }

        // Kanan: Detail QR Code yang dipilih
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (uiState.selectedTicket != null) {
                    QrCodeDetailPanel(ticket = uiState.selectedTicket!!)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Pilih tiket di samping untuk melihat QR Code", color = Color.Gray)
                    }
                }
            }
        }
    }
}