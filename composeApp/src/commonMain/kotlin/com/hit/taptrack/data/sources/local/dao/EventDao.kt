package com.hit.taptrack.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hit.taptrack.data.sources.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Upsert
    suspend fun upsertEvents(events: List<EventEntity>)

    @Query("SELECT * FROM events ORDER BY startDatetime ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("DELETE FROM events")
    suspend fun clearAllEvents()

    @Query("UPDATE events SET status = :newStatus WHERE id = :eventId")
    suspend fun updateEventStatusLocal(eventId: String, newStatus: String)
}