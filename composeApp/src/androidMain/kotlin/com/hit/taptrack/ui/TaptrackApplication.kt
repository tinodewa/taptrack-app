package com.hit.taptrack.ui

import android.app.Application
import com.hit.taptrack.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TaptrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initializes Logging for Android (Logcat)
        Napier.base(DebugAntilog())

        initKoin{
            androidContext(this@TaptrackApplication)
            androidLogger()
        }
    }
}