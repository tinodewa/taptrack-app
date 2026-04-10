package com.hit.taptrack.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val startDatetime: String,
    val status: String,
    val lastUpdated: Long = Clock.System.now().toEpochMilliseconds()
)