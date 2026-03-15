package com.hit.taptrack.ui.features.auth.register

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.ui.features.auth.register.states.RegisterEvent
import com.hit.taptrack.ui.features.auth.register.states.RegisterUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FullNameChanged -> _uiState.update { it.copy(fullName = event.name) }
            is RegisterEvent.EmailChanged -> _uiState.update { it.copy(email = event.email) }
            is RegisterEvent.PasswordChanged -> _uiState.update { it.copy(password = event.password) }
            is RegisterEvent.BloodTypeChanged -> _uiState.update { it.copy(bloodType = event.bloodType) }
            is RegisterEvent.EmergencyContactChanged -> _uiState.update { it.copy(emergencyContact = event.contact) }
            is RegisterEvent.VehicleChanged -> _uiState.update { it.copy(vehicle = event.vehicle) }
            RegisterEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            RegisterEvent.SubmitRegister -> performRegister()
        }
    }

    private fun performRegister() {
        val state = _uiState.value

        // Validasi Form
        if (state.fullName.isBlank() || state.email.isBlank() || state.password.isBlank() ||
            state.bloodType.isBlank() || state.emergencyContact.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Mohon lengkapi semua data wajib") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch {
            try {
                // TODO: Eksekusi API POST /api/v1/auth/register di sini
                delay(1500) // Simulasi loading

                _uiState.update { it.copy(isLoading = false, isRegisterSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Gagal melakukan registrasi")
                }
            }
        }
    }
}