package com.hit.taptrack.data.sources.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.hit.taptrack.data.sources.local.dao.CheckpointDao
import com.hit.taptrack.data.sources.local.dao.EventDao
import com.hit.taptrack.data.sources.local.dao.ParticipantDao
import com.hit.taptrack.data.sources.local.dao.ScanLogDao
import com.hit.taptrack.data.sources.local.dao.TicketDao
import com.hit.taptrack.data.sources.local.entity.CheckpointEntity
import com.hit.taptrack.data.sources.local.entity.EventEntity
import com.hit.taptrack.data.sources.local.entity.ParticipantEntity
import com.hit.taptrack.data.sources.local.entity.ScanLogEntity
import com.hit.taptrack.data.sources.local.entity.TicketEntity

@Database(entities = [
    EventEntity::class,
    TicketEntity::class,
    ScanLogEntity::class,
    ParticipantEntity::class,
    CheckpointEntity::class
], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun ticketDao(): TicketDao
    abstract fun scanLogDao(): ScanLogDao
    abstract fun participantDao(): ParticipantDao
    abstract fun checkpointDao(): CheckpointDao
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