package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class TestApiResponse(
    @SerialName("db")
    var db: String? = null,
    @SerialName("message")
    var message: String? = null,
    @SerialName("status")
    var status: String? = null
)