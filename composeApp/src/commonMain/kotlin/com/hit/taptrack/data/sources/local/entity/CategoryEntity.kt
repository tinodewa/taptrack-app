package com.hit.taptrack.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entitas Baru
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val categoryName: String
)