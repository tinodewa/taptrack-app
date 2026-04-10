package com.hit.taptrack.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hit.taptrack.data.sources.local.entity.ParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipantDao {
    @Upsert
    suspend fun upsertParticipants(participants: List<ParticipantEntity>)

    @Query("SELECT * FROM participants WHERE eventId = :eventId ORDER BY raceNumber ASC")
    fun getParticipantsByEvent(eventId: String): Flow<List<ParticipantEntity>>

    @Query("DELETE FROM participants WHERE eventId = :eventId")
    suspend fun deleteParticipantsByEvent(eventId: String)
}