package com.hit.taptrack.di

import com.hit.taptrack.platform.DesktopNetworkMonitor
import com.hit.taptrack.platform.NetworkMonitor
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module= module {
    single<NetworkMonitor> { DesktopNetworkMonitor() }
}