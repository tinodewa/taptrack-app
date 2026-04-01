package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("message")
    var message: String? = null,
    @SerialName("role")
    var role: String? = null,
    @SerialName("token")
    var token: String? = null,
    @SerialName("profile_id")
    var profileId: String? = null,
    @SerialName("error")
    var error: String? = null
)