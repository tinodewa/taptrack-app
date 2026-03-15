package com.hit.taptrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryRed,
    onPrimary = SurfaceLight, // Warna teks di atas tombol primary
    secondary = SecondaryBlue,
    onSecondary = SurfaceLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight, // Warna teks utama di atas background
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    error = ErrorColor
)

private val DarkColors = darkColorScheme(
    primary = PrimaryRedDark, // Agak digelapkan sedikit untuk dark mode agar tidak terlalu menusuk mata
    onPrimary = TextPrimaryDark,
    secondary = SecondaryBlueLight, // Biru diterangkan sedikit agar kontras di background gelap
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    error = ErrorColor
)

@Composable
fun TapTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Otomatis deteksi tema sistem
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        // shapes = AppShapes // (Opsional) Jika nanti ingin mengatur radius border tombol secara global
        content = content
    )
}