package com.hit.taptrack.data.sources.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    // Tentukan lokasi penyimpanan file .db di komputer (misal di folder user)
    val dbFile = File(System.getProperty("java.io.tmpdir"), "taptrack.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
}