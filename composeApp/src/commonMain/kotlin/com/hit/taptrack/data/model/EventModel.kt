package com.hit.taptrack.data.model

import com.hit.taptrack.common.state.EventStatus

// Representasi DTO (Data Transfer Object) dari GET /api/v1/events
data class EventModel(
    val id: String,
    val name: String,
    val date: String,
    val location: String,
    val status: EventStatus,
    val participantCount: Int? = 0
)