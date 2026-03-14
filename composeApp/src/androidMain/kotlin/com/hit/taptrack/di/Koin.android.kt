package com.hit.taptrack.di

import com.hit.taptrack.platform.AndroidNetworkMonitor
import com.hit.taptrack.platform.NetworkMonitor
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<NetworkMonitor> { AndroidNetworkMonitor(get()) }
}