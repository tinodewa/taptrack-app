package com.hit.taptrack.data.sources.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

// Perlu menyimpan context aplikasi saat start up
lateinit var appContext: Context

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = appContext.getDatabasePath("taptrack.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}