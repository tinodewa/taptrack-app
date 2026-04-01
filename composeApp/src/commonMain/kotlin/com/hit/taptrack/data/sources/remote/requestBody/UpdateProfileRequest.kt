package com.hit.taptrack.data.sources.remote.requestBody

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("full_name")
    var fullName: String? = null,
    @SerialName("blood_type")
    var bloodType: String? = null,
    @SerialName("emergency_contact")
    var emergencyContact: String? = null,
    @SerialName("vehicle_class")
    var vehicleClass: String? = null
)