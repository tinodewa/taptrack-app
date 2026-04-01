package com.hit.taptrack.di

import android.content.Context
import com.hit.taptrack.platform.AndroidNetworkMonitor
import com.hit.taptrack.platform.NetworkMonitor
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<NetworkMonitor> { AndroidNetworkMonitor(get()) }

    single<Settings> {
        val context = get<Context>()
        val sharedPrefs = context.getSharedPreferences("taptrack_settings", Context.MODE_PRIVATE)
        SharedPreferencesSettings(sharedPrefs)
    }
}