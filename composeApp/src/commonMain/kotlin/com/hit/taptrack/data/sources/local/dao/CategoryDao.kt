package com.hit.taptrack.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hit.taptrack.data.sources.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun upsertCategories(categories: List<CategoryEntity>)

    // Mengambil daftar kategori untuk acara tertentu (Berguna untuk Filter Leaderboard nanti)
    @Query("SELECT * FROM categories WHERE eventId = :eventId ORDER BY categoryName ASC")
    fun getCategoriesByEventFlow(eventId: String): Flow<List<CategoryEntity>>
}