package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CreateEventResponse(
    @SerialName("event_id")
    var eventId: String? = null,
    @SerialName("message")
    var message: String? = null,
    @SerialName("status")
    var status: String? = null
)

@Serializable
data class OverrideParticipantStatusResponse(
    @SerialName("message")
    var message: String? = null,
    @SerialName("registration_id")
    var registrationId: String? = null,
    @SerialName("status")
    var status: String? = null,
    @SerialName("error")
    var error: String? = null
)