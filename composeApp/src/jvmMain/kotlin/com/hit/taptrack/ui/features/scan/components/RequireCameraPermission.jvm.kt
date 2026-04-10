package com.hit.taptrack.ui.features.scan.components

import androidx.compose.runtime.Composable

@Composable
actual fun RequireCameraPermission(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable (requestPermission: () -> Unit) -> Unit
) {
    // Desktop langsung lolos ke fungsi kamera
    onPermissionGranted()
}