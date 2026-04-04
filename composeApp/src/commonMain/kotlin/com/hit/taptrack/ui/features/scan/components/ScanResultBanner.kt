package com.hit.taptrack.ui.features.scan.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.scan.states.ScanResult

@Composable
fun ScanResultBanner(result: ScanResult, onDismiss: () -> Unit) {
    val isSuccess = result is ScanResult.Success
    val bgColor = if (isSuccess) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
    val icon = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    if (result is ScanResult.Success) {
                        Text(
                            "BERHASIL: POSISI TERCATAT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            result.message,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                        // Akan kita tambahkan response racrenumber dan racername pada backend nanti
//                        Text(
//                            "#${result.racerNumber} - ${result.racerName}",
//                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
//                            color = Color.White
//                        )
                        Text(
                            "Waktu: ${result.time}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    } else if (result is ScanResult.Error) {
                        Text(
                            "PEMINDAIAN GAGAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            result.message,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = bgColor
                )
            ) {
                Text("LANJUT PINDAI BERIKUTNYA")
            }
        }
    }
}