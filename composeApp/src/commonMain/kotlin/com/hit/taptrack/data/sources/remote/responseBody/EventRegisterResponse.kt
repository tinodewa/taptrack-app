package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class EventRegisterResponse(
    @SerialName("message")
    var message: String? = null,
    @SerialName("qr_token_hash")
    var qrTokenHash: String? = null,
    @SerialName("race_number")
    var raceNumber: String? = null,
    @SerialName("registration_id")
    var registrationId: String? = null,
    @SerialName("details")
    var details: String? = null,
    @SerialName("error")
    var error: String? = null
)