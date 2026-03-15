package com.hit.taptrack.ui.features.myTicket.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hit.taptrack.data.model.TicketModel

// --- KOMPONEN POPUP/DIALOG QR (Untuk Mobile) ---
@Composable
fun QrCodeDialog(ticket: TicketModel, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            QrCodeDetailPanel(ticket = ticket, isDialog = true)
        }
    }
}