package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class EventCheckpointsResponse(
    @SerialName("data")
    var data: ArrayList<CheckpointDetail> = arrayListOf(),
    @SerialName("event_id")
    var eventId: String? = null,
    @SerialName("message")
    var message: String? = null
)

@Serializable
data class CheckpointDetail(
    @SerialName("id")
    var id: String? = null,
    @SerialName("checkpoint_name")
    var checkpointName: String? = null,
    @SerialName("type_flag")
    var typeFlag: String? = null,
    @SerialName("sequence_order")
    var sequenceOrder: Int? = null,
    @SerialName("stage_number")
    var stageNumber: Int? = null
)

@Serializable
data class CategoryDetail(
    @SerialName("id")
    var id: String? = null,
    @SerialName("category_name")
    var categoryName: String? = null,
)