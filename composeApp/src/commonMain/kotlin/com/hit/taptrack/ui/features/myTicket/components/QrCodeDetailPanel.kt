package com.hit.taptrack.ui.features.myTicket.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hit.taptrack.data.model.TicketModel
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

// --- PANEL DETAIL QR (Bisa dipakai di Dialog Mobile maupun Split View Desktop) ---
@Composable
fun QrCodeDetailPanel(ticket: TicketModel, isDialog: Boolean = false) {
    Column(
        modifier = Modifier.padding(32.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Siapkan untuk di-scan",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Placeholder QR Code (Wajib background putih kontras tinggi agar bisa di-scan scanner fisik)
        Box(
            modifier = Modifier
                .size(if (isDialog) 200.dp else 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = rememberQrCodePainter(ticket.qrCodePayload),
                    contentDescription = "QR code referring to the example.com website"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = ticket.eventName,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
//        Text(text = "Kelas: ${ticket.category}", style = MaterialTheme.typography.bodyLarge)
//        Text(
//            text = "ID: ${ticket.registrationId}",
//            style = MaterialTheme.typography.labelMedium,
//            color = MaterialTheme.colorScheme.primary
//        )
    }
}