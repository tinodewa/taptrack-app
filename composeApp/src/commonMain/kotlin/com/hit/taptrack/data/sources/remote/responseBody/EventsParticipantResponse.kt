package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class EventsParticipantResponse(
    @SerialName("data")
    var data: ArrayList<EventsParticipation> = arrayListOf(),
    @SerialName("message")
    var message: String? = null
)

@Serializable
data class SingleEventParticipantResponse(
    @SerialName("data")
    var data: EventsParticipation? = null,
    @SerialName("message")
    var message: String? = null
)

@Serializable
data class EventsParticipation(
    @SerialName("event_id")
    var eventId: String? = null,
    @SerialName("title")
    var title: String? = null,
    @SerialName("start_datetime")
    var startDatetime: String? = null,
    @SerialName("race_number")
    var raceNumber: String? = null,
    @SerialName("registration_status")
    var registrationStatus: String? = null,
    @SerialName("qr_token_hash")
    var qrTokenHash: String? = null
)