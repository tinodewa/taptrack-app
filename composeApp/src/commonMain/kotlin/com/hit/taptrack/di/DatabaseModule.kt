package com.hit.taptrack.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.hit.taptrack.data.sources.local.AppDatabase
import com.hit.taptrack.data.sources.local.getDatabaseBuilder
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val databaseModule = module {
    // 1. Inisialisasi Database
    single<AppDatabase> {
        // getDatabaseBuilder() akan otomatis memanggil versi Android atau Desktop
        // tergantung aplikasi sedang dijalankan di mana!
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver()) // Driver universal pengganti SQLite bawaan OS
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }

    // 2. Sediakan EventDao agar bisa dipanggil oleh Repository
    single { get<AppDatabase>().eventDao() }
}