package com.hit.taptrack.ui.features.initial.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.eventDiscovery.EventDiscoveryScreen
import com.hit.taptrack.ui.features.initial.landing.LandingScreen
import com.hit.taptrack.utils.helpers.UserSettings
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // 1. Suntikkan UserSettings menggunakan Koin
        val userSettings: UserSettings = koinInject()

        // 2. Logika Pengecekan Kredensial di Background
        LaunchedEffect(Unit) {
            // Beri sedikit delay agar logo aplikasi sempat terlihat oleh pengguna (branding)
            // Di dunia nyata, delay ini bisa dihapus jika tidak diperlukan
            delay(1500)

            // Cek apakah pengguna sudah memiliki token JWT yang tersimpan
            if (userSettings.isLoggedIn()) {
                // Jika sudah login, langsung buang Splash Screen dan masuk ke aplikasi utama
                // (Ganti dengan nama Screen utama kamu, misal EventDiscoveryScreen atau HomeScreen)
                navigator.replaceAll(EventDiscoveryScreen())
            } else {
                // Jika belum login (atau baru pertama instal), arahkan ke Landing Page
                navigator.replaceAll(LandingScreen())
            }
        }

        // --- UI SPLASH SCREEN ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Ikon / Logo Aplikasi
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SportsScore,
                        contentDescription = "Logo TapTrack",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "TAPTRACK",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(
                    text = "Sistem Manajemen Balapan",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(48.dp))

                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp
                )
            }
        }
    }
}