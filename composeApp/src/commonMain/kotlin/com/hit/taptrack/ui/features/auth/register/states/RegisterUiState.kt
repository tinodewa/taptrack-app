package com.hit.taptrack.ui.features.auth.register.states

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val bloodType: String = "",
    val emergencyContact: String = "",
    val vehicle: String = "", // Contoh: "Yamaha YZ250F"
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccess: Boolean = false
)