package com.hit.taptrack.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    // Kita gunakan eventId sebagai PrimaryKey karena satu pembalap
    // idealnya hanya punya satu tiket per acara.
    @PrimaryKey val eventId: String,
    val title: String,
    val startDatetime: String,
    val raceNumber: String,
    val registrationStatus: String,
    val qrTokenHash: String
)