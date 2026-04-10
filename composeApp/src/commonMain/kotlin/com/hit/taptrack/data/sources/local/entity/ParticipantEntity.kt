package com.hit.taptrack.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "participants")
data class ParticipantEntity(
    @PrimaryKey val registrationId: String,
    val eventId: String, // To filter participants by event
    val raceNumber: String,
    val racerName: String,
    val vehicleClass: String,
    val registrationStatus: String,
    val lastCheckpoint: String?
)