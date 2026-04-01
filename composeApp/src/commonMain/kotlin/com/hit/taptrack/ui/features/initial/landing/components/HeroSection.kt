package com.hit.taptrack.ui.features.initial.landing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HeroSection(isDesktop: Boolean = false) {
    Column(
        horizontalAlignment = if (isDesktop) Alignment.Start else Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "APLIKASI RESMI",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Dominasi Lintasan dengan TapTrack",
            style = if (isDesktop) MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black)
            else MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            textAlign = if (isDesktop) TextAlign.Start else TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Platform pencatat waktu presisi dan manajemen balapan lintas platform terpadu untuk pembalap dan panitia.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = if (isDesktop) TextAlign.Start else TextAlign.Center,
            color = Color.Gray
        )
    }
}