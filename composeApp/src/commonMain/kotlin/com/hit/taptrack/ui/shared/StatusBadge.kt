package com.hit.taptrack.ui.features.eventDiscovery.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.common.state.EventStatus

// Komponen Label Status Dinamis
@Composable
fun StatusBadge(
    status: EventStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, text) = when (status) {
        EventStatus.UPCOMING -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary,
            "MENDATANG"
        )

        EventStatus.ACTIVE -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "BERLANGSUNG"
        ) // Hijau untuk "Live"
        EventStatus.FINISHED -> Triple(
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.onSurfaceVariant,
            "SELESAI"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}