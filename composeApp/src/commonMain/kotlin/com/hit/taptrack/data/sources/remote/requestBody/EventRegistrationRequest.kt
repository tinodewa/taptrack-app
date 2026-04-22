package com.hit.taptrack.data.sources.remote.requestBody

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRegistrationRequest(
    @SerialName("event_id")
    var eventId: String? = null,
    @SerialName("race_number")
    var raceNumber: String? = null,
    @SerialName("category_id")
    var categoryId: String? = null
)