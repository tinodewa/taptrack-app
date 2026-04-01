package com.hit.taptrack.ui.features.initial.landing.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun FeaturesSection(isDesktop: Boolean = false) {
    val arrangement = if (isDesktop) Arrangement.Start else Arrangement.Center

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        FeatureItem(
            icon = Icons.Default.Leaderboard,
            title = "Live Leaderboard",
            desc = "Pantau catatan waktu dan klasemen secara real-time dari sirkuit."
        )
        FeatureItem(
            icon = Icons.Default.QrCode,
            title = "Tiket QR Digital",
            desc = "Pendaftaran instan dan verifikasi cepat di garis start menggunakan QR Code."
        )
        FeatureItem(
            icon = Icons.Default.Bolt,
            title = "Presisi Tinggi",
            desc = "Sinkronisasi waktu tingkat milidetik untuk menentukan sang juara sejati."
        )
    }
}