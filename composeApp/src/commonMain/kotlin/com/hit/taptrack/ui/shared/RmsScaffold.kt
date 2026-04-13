package com.hit.taptrack.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hit.taptrack.utils.AppConfig
import sp.bvantur.inspektify.ktor.InspektifyKtor

@Composable
fun RmsScaffold(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    // 1. Bungkus dengan Box agar kita bisa menimpa UI paling depan
    Box(modifier = Modifier.fillMaxSize()) {

        // 2. Scaffold Utama (Tidak ada yang berubah di sini)
        Scaffold(
            modifier = modifier,
            containerColor = backgroundColor,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = floatingActionButton
        ) { innerPadding ->
            content(innerPadding)
        }

        // 3. Tombol Debug Global (Hanya dirender jika IS_DEBUG_MODE = true)
        if (AppConfig.IS_DEBUG_MODE) {
            FloatingActionButton(
                onClick = { InspektifyKtor.startInspektify() },
                modifier = Modifier
                    .align(Alignment.BottomStart) // Taruh di KIRI Bawah (BottomStart)
                    .padding(start = 16.dp, bottom = 80.dp), // Angkat sedikit agar tidak tertutup navigasi OS
                containerColor = MaterialTheme.colorScheme.errorContainer, // Warna merah bata agar mencolok
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = "Buka Log Jaringan"
                )
            }
        }
    }
}