package com.hit.taptrack.ui.features.auth.register

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.repositories.AuthRepository
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.data.sources.remote.requestBody.AuthRequest
import com.hit.taptrack.data.sources.remote.requestBody.UpdateProfileRequest
import com.hit.taptrack.ui.features.auth.register.states.RegisterEvent
import com.hit.taptrack.ui.features.auth.register.states.RegisterUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterScreenModel(
    private val authRepository: AuthRepository
) : ScreenModel {

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

        // Validasi input
        if (state.email.isBlank() || state.password.isBlank() || state.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama, Email, dan Password wajib diisi.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch {
            val registerReq = AuthRequest(email = state.email, password = state.password)
            val profileReq = UpdateProfileRequest(
                fullName = state.fullName,
                bloodType = state.bloodType,
                emergencyContact = state.emergencyContact,
                vehicleClass = state.vehicle
            )

            val result = authRepository.registerAndSetupProfile(registerReq, profileReq)

            when (result) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(isLoading = false, isRegisterSuccess = true) }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}