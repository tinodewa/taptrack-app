package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ScanQrResponse(
    @SerialName("message")
    var message: String? = null,
    @SerialName("scan_log_id")
    var scanLogId: String? = null,
    @SerialName("sync_status")
    var syncStatus: String? = null
)