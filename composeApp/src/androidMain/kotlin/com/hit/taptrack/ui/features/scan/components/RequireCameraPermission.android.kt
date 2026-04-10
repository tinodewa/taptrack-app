package com.hit.taptrack.ui.features.scan.components

import androidx.compose.runtime.Composable

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun RequireCameraPermission(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable (requestPermission: () -> Unit) -> Unit
) {
    val context = LocalContext.current

    // Cek status izin saat ini
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher untuk memunculkan popup dialog izin sistem Android
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )

    // Saat layar pertama kali dibuka, langsung minta izin jika belum ada
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Tampilkan UI sesuai status izin
    if (hasPermission) {
        onPermissionGranted()
    } else {
        onPermissionDenied { launcher.launch(Manifest.permission.CAMERA) }
    }
}