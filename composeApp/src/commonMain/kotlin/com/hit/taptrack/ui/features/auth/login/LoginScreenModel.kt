package com.hit.taptrack.ui.features.auth.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.data.sources.remote.requestBody.AuthRequest
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

        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email dan Password tidak boleh kosong") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch {
            val request = AuthRequest(email = currentEmail, password = currentPassword)

            // Panggil fungsi login di repository
            val result = tapTrackRepository.login(request)

            // Gunakan 'when' untuk menangani ResultState buatanmu
            when (result) {
                is ResultState.Success -> {
                    // Login Sukses!
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                }

                is ResultState.Error -> {
                    // Login Gagal! (Entah karena 401, timeout, dsb)
                    // Asumsi ResultState.Error memiliki properti 'message' dan 'statusCode'
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}