package com.hit.taptrack.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hit.taptrack.data.sources.local.entity.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Upsert
    suspend fun upsertTickets(tickets: List<TicketEntity>)

    // Ambil semua tiket, bisa diurutkan berdasarkan waktu balapan
    @Query("SELECT * FROM tickets ORDER BY startDatetime ASC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE eventId = :eventId LIMIT 1")
    fun getTicketByEventIdFlow(eventId: String): Flow<TicketEntity?>

    // Fitur tambahan: Hapus semua tiket (sangat berguna saat user Logout!)
    @Query("DELETE FROM tickets")
    suspend fun clearAllTickets()
}