package com.hit.taptrack.data.model

data class UserProfileModel(
    val licenseId: String,
    val fullName: String,
    val email: String,
    val bloodType: String,
    val emergencyContact: String,
    val vehicle: String,
    val role: String // misal: "RACER" atau "COMMITTEE"
)