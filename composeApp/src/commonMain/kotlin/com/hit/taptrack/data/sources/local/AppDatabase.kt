package com.hit.taptrack.data.sources.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.hit.taptrack.data.sources.local.dao.EventDao
import com.hit.taptrack.data.sources.local.dao.TicketDao
import com.hit.taptrack.data.sources.local.entity.EventEntity
import com.hit.taptrack.data.sources.local.entity.TicketEntity

@Database(entities = [
    EventEntity::class,
    TicketEntity::class
], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun ticketDao(): TicketDao
}

// 2. Tambahkan blok expect object ini di bagian bawah file
// Catatan: KSP akan secara ajaib dan otomatis men-generate 'actual' implementasinya
// di belakang layar saat kamu melakukan build project!
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

// Fungsi expect ini yang akan memaksa tiap platform membuat builder-nya sendiri
expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>