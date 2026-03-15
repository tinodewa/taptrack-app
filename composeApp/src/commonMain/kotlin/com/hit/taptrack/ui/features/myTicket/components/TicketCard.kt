package com.hit.taptrack.ui.features.myTicket.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.data.model.TicketModel

// --- KOMPONEN KARTU TIKET ---
@Composable
fun TicketCard(ticket: TicketModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Bagian Kiri (Info)
            Column(
                modifier = Modifier.weight(1f).padding(16.dp)
            ) {
                Text(text = ticket.eventName, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = ticket.date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                Text(text = ticket.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(16.dp))

                // Status Badge Lokal untuk Tiket
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = ticket.status, style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }

            // Garis Putus-putus (Visual Tiket)
            Box(
                modifier = Modifier.fillMaxHeight().width(2.dp).padding(vertical = 16.dp)
                    .background(Color.White.copy(alpha = 0.5f))
            )

            // Bagian Kanan (Aksi QR / Nomor Start)
            Column(
                modifier = Modifier.width(100.dp).fillMaxHeight().background(MaterialTheme.colorScheme.primary),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "START", style = MaterialTheme.typography.labelSmall, color = Color.White)
                Text(text = "#${ticket.racerNumber}", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black), color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Icon(Icons.Default.QrCode2, contentDescription = "Tampilkan QR", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
    }
}