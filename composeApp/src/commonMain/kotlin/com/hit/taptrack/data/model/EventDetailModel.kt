package com.hit.taptrack.data.model

import com.hit.taptrack.common.state.EventStatus

// Model data detail (DTO dari GET /api/v1/events/:event_id)
data class EventDetailModel(
    val id: String,
    val name: String,
    val description: String,
    val date: String,
    val status: EventStatus,
    val categories: List<CategoryUiModel>, // Menampung nama kategori
    val stages: List<StageUiModel> // Menampung rute per SS
)