package com.hit.taptrack.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_logs")
data class ScanLogEntity(
    // Kita gunakan kombinasi timestamp dan hash sebagai ID unik lokal
    @PrimaryKey val localLogId: String,
    val eventId: String,
    val qrTokenHash: String,
    val checkpointId: String,
    val absoluteTimestamp: String,
    val manualOverrideFlag: String,
    val syncStatus: String = "PENDING" // Bisa berisi: "PENDING", "SYNCED", "FAILED"
)