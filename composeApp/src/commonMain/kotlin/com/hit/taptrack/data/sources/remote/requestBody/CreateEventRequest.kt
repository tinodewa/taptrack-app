package com.hit.taptrack.data.sources.remote.requestBody

import com.hit.taptrack.data.sources.remote.responseBody.CheckpointDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateEventRequest(
    @SerialName("title")
    var title: String? = null,
    @SerialName("start_datetime")
    var startDatetime: String? = null,
    @SerialName("description_text")
    var descriptionText: String? = null,
    @SerialName("checkpoints")
    var data: ArrayList<CheckpointDetail> = arrayListOf(),
)

@Serializable
data class ChangeEventStatusRequest(
    @SerialName("status")
    var status: String? = null, // UPCOMING, ACTIVE, FINISHED
)

@Serializable
data class ChangeParticipantStatusRequest(
    @SerialName("status")
    var status: String? = null, // DISQUALIFIED, COMPLETED, REGISTERED, ON_TRACK
)
