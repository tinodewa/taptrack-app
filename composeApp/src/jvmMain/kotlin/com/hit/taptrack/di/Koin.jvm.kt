package com.hit.taptrack.di

import com.hit.taptrack.platform.DesktopNetworkMonitor
import com.hit.taptrack.platform.NetworkMonitor
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val platformModule: Module = module {
    single<NetworkMonitor> { DesktopNetworkMonitor() }
    
    single<Settings> {
        val prefs = Preferences.userRoot().node("taptrack_settings")
        PreferencesSettings(prefs)
    }
}