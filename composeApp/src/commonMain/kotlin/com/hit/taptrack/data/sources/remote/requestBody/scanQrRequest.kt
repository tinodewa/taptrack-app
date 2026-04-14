package com.hit.taptrack.data.sources.remote.requestBody

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanQrRequest(
    @SerialName("qr_token_hash")
    var qrTokenHash: String? = null,
    @SerialName("checkpoint_id")
    var checkpointId: String? = null,
    @SerialName("absolute_timestamp")
    var absoluteTimestamp: String? = null,
    @SerialName("manual_override_flag")
    var manualOverrideFlag: Boolean = false,
)