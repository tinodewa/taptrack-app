package com.hit.taptrack

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.hit.taptrack.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() = application {

    // Initializes Logging for Desktop (System.out)
    Napier.base(DebugAntilog())

    // Init Koin
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "TapTrack",
    ) {
        App()
    }
}