package com.hit.taptrack.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hit.taptrack.data.sources.local.entity.ScanLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ScanLogEntity)

    @Query("UPDATE scan_logs SET syncStatus = :newStatus WHERE localLogId = :id")
    suspend fun updateSyncStatus(id: String, newStatus: String)

    // Untuk mengambil daftar yang perlu di-sync ke server nanti
    @Query("SELECT * FROM scan_logs WHERE syncStatus = 'PENDING'")
    suspend fun getPendingScans(): List<ScanLogEntity>

    // Flow agar UI bisa menampilkan "Ada 5 tiket menunggu sinkronisasi" secara real-time
    @Query("SELECT COUNT(*) FROM scan_logs WHERE eventId = :eventId AND syncStatus = 'PENDING'")
    fun getPendingCountFlow(eventId: String): Flow<Int>
}