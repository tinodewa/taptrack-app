package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RegisterResponse(
    @SerialName("message")
    var message: String? = null,
    @SerialName("user_id")
    var userId: String? = null
)