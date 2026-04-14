package com.hit.taptrack.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkpoints")
data class CheckpointEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val checkpointName: String,
    val typeFlag: String,
    val sequenceOrder: Int
)