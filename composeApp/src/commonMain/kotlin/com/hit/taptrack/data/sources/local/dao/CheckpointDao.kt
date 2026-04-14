package com.hit.taptrack.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hit.taptrack.data.sources.local.entity.CheckpointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckpointDao {
    @Upsert
    suspend fun upsertCheckpoints(checkpoints: List<CheckpointEntity>)

    @Query("SELECT * FROM checkpoints WHERE eventId = :eventId ORDER BY sequenceOrder ASC")
    fun getCheckpointsByEvent(eventId: String): Flow<List<CheckpointEntity>>
}