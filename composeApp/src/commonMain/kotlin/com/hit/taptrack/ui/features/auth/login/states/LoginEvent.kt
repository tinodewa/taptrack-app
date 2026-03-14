package com.hit.taptrack.ui.features.auth.login.states

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object SubmitLogin : LoginEvent()
    object ClearError : LoginEvent() // Tambahan untuk mematikan pemicu Snackbar
}