package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class EventsResponse(
    @SerialName("data")
    var data: ArrayList<Events> = arrayListOf(),
    @SerialName("message")
    var message: String? = null
)

@Serializable
data class Events(
    @SerialName("id")
    var id: String? = null,
    @SerialName("title")
    var title: String? = null,
    @SerialName("start_datetime")
    var startDatetime: String? = null,
    @SerialName("status")
    var status: String? = null
)

@Serializable
data class EventDetailResponse(
    @SerialName("data")
    var data: EventDetail? = EventDetail(),
    @SerialName("message")
    var message: String? = null
)

@Serializable
data class EventDetail(
    @SerialName("id")
    var id: String? = null,
    @SerialName("title")
    var title: String? = null,
    @SerialName("start_datetime")
    var startDatetime: String? = null,
    @SerialName("status")
    var status: String? = null,
    @SerialName("description_text")
    var descriptionText: String? = null,
    @SerialName("categories")
    var categories: List<CategoryDetail> = emptyList(),
    @SerialName("checkpoints")
    var checkpoints: List<CheckpointDetail> = emptyList()
)