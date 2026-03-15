package com.hit.taptrack.data.model

import com.hit.taptrack.common.state.EventStatus

// Model data detail (DTO dari GET /api/v1/events/:event_id)
data class EventDetailModel(
    val id: String,
    val name: String,
    val description: String,
    val date: String,
    val location: String,
    val rules: String,
    val categories: List<String>, // Kelas balapan, misal: "Bebek 4T 130cc", "Sport 250cc"
    val status: EventStatus, // Menggunakan enum dari penemuan acara
    val registeredRacers: Int,
    val maxCapacity: Int
)