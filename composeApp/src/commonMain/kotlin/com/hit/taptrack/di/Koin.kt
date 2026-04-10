package com.hit.taptrack.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

// 1. Fungsi untuk memulai Koin (panggil di main entry point tiap platform)
fun initKoin(config: KoinAppDeclaration = {}) : KoinApplication = startKoin {
    // Execute platform-specific configuration (modules, androidContext, etc.)
    config.invoke(this)

    modules(
        databaseModule,  // Modul untuk Room
        commonModule,    // Modul umum (ViewModel/ScreenModel)
        ktorfitModule,   // Modul untuk Ktorfit
        platformModule, // <--- Ini modul rahasia yang beda tiap platform
    )
}

// 3. Definisi Expect: "Saya harap ada variabel bernama platformModule di setiap platform"
expect val platformModule: Module