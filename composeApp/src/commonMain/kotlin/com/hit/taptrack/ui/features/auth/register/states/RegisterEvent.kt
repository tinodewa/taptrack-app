package com.hit.taptrack.ui.features.auth.register.states

sealed class RegisterEvent {
    data class FullNameChanged(val name: String) : RegisterEvent()
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class BloodTypeChanged(val bloodType: String) : RegisterEvent()
    data class EmergencyContactChanged(val contact: String) : RegisterEvent()
    data class VehicleChanged(val vehicle: String) : RegisterEvent()
    object SubmitRegister : RegisterEvent()
    object ClearError : RegisterEvent()
}