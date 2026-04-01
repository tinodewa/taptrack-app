package com.hit.taptrack.ui.features.auth.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.ui.features.auth.login.states.LoginEvent
import com.hit.taptrack.ui.features.auth.login.states.LoginUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val tapTrackRepository: TapTrackRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        getHealth()
    }

    fun getHealth() {
        screenModelScope.launch {
            // get api response
            val healthResponse = tapTrackRepository.getHealth()

            // print to log
            Napier.d("Health Response: $healthResponse" )
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, errorMessage = null) }
            }

            is LoginEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }

            is LoginEvent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            LoginEvent.SubmitLogin -> performLogin()

        }
    }

    private fun performLogin() {
        val currentEmail = _uiState.value.email
        val currentPassword = _uiState.value.password

        // Validasi input sederhana
        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email dan Password tidak boleh kosong") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch {
            try {
                // TODO: Ganti dengan pemanggilan fungsi repository / API POST /api/v1/auth/login
                // val response = authRepository.login(currentEmail, currentPassword)

                // Simulasi delay jaringan sementara API belum tersambung
                delay(1500)

                // Jika sukses:
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }

            } catch (e: Exception) {
                // Jika gagal:
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Terjadi kesalahan saat login"
                    )
                }
            }
        }
    }
}