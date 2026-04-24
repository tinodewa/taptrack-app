package com.hit.taptrack.data.sources.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    // Save DB file in a persistent location instead of temp folder
    val appDir = File(System.getProperty("user.home"), ".taptrack")
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    val dbFile = File(appDir, "taptrack.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
}