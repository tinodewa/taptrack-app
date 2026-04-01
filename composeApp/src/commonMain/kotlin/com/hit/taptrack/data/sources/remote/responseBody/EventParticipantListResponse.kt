package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class EventParticipantListResponse(
    @SerialName("data")
    var data: ArrayList<ParticipantDetail> = arrayListOf(),
    @SerialName("message")
    var message: String? = null
)

@Serializable
data class ParticipantDetail(
    @SerialName("registration_id")
    var registrationId: String? = null,
    @SerialName("race_number")
    var raceNumber: String? = null,
    @SerialName("racer_name")
    var racerName: String? = null,
    @SerialName("vehicle_class")
    var vehicleClass: String? = null,
    @SerialName("registration_status")
    var registrationStatus: String? = null,
    @SerialName("last_checkpoint")
    var lastCheckpoint: String? = null
)