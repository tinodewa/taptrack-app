package com.hit.taptrack.ui.features.scan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun QRScannerView(modifier: Modifier, onScan: (String) -> Unit) {
    Box(
        modifier = modifier.background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { onScan("TAPTRACK-REG-9912A") }) {
            Text("Simulate Scan (Desktop)")
        }
    }
}