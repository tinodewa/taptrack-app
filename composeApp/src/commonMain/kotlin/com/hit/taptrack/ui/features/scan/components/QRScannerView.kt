package com.hit.taptrack.ui.features.scan.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun QRScannerView(modifier: Modifier, onScan: (String) -> Unit)