package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ProfileResponse(
    @SerialName("data")
    var data: Profile? = Profile()
)

@Serializable
data class Profile(
    @SerialName("id")
    var id: String? = null,
    @SerialName("user_id")
    var userId: String? = null,
    @SerialName("full_name")
    var fullName: String? = null,
    @SerialName("blood_type")
    var bloodType: String? = null,
    @SerialName("emergency_contact")
    var emergencyContact: String? = null,
    @SerialName("vehicle_class")
    var vehicleClass: String? = null
)

@Serializable
data class UpdateProfileResponse(
    @SerialName("message")
    var message: String? = null,
    @SerialName("profile_id")
    var profileId: String? = null
)