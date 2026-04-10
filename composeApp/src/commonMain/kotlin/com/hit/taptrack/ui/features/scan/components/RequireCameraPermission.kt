package com.hit.taptrack.ui.features.scan.components

import androidx.compose.runtime.Composable

@Composable
expect fun RequireCameraPermission(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable (requestPermission: () -> Unit) -> Unit
)