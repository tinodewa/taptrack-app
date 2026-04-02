package com.hit.taptrack.ui.features.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.data.model.UserProfileModel
import com.hit.taptrack.ui.features.profile.states.ProfileEvent
import com.hit.taptrack.ui.features.profile.states.ProfileUiState
import com.hit.taptrack.utils.helpers.UserSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val userSettings: UserSettings
) : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LogoutClicked -> processLogout()
            ProfileEvent.RefreshProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Simulasi ambil data profil (GET /api/v1/users/me) atau dari Multiplatform Settings
            delay(800)

            val dummyProfile = UserProfileModel(
                licenseId = "T-RACER-2026-X99",
                fullName = "Andi Gilang",
                email = "andi.racer@taptrack.com",
                bloodType = "O",
                emergencyContact = "08123456789 (Istri)",
                vehicle = "Yamaha YZ250F",
                // Ambil role yang sebenarnya dari local storage untuk simulasi UI yang lebih nyata
                role = userSettings.getRole()
            )

            _uiState.update { it.copy(isLoading = false, profile = dummyProfile) }
        }
    }

    private fun processLogout() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Eksekusi penghapusan Token JWT, Role, dan Profile ID
            userSettings.clearAuthData()

            // 2. Simulasi proses agar animasi loading terlihat natural
            delay(500)

            // 3. Ubah state untuk memicu LaunchedEffect di UI
            _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
        }
    }
}