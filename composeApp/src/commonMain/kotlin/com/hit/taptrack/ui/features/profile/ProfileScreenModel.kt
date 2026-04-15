package com.hit.taptrack.ui.features.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.model.UserProfileModel
import com.hit.taptrack.data.repositories.AuthRepository
import com.hit.taptrack.data.repositories.TapTrackRepository
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
    private val userSettings: UserSettings,
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LogoutClicked -> processLogout()
            ProfileEvent.RefreshProfile -> loadProfile(isRefresh = true)
        }
    }

    private fun loadProfile(isRefresh: Boolean = false) {
        screenModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            // Panggil API dari Repository
            val result = authRepository.getProfile()

            when (result) {
                is ResultState.Success -> {
                    val dto = result.data

                    // Mapping dari data API (Profile) ke data UI (UserProfileModel)
                    val actualProfile = UserProfileModel(
                        licenseId = dto.id ?: "T-RACER-UNKNOWN", // Fallback jika ID null
                        fullName = dto.fullName ?: "Pembalap Tanpa Nama",
                        email = "Email disembunyikan", // API saat ini tidak mengirimkan email
                        bloodType = dto.bloodType ?: "-",
                        emergencyContact = dto.emergencyContact ?: "-",
                        vehicle = dto.vehicleClass ?: "Belum diisi",
                        role = userSettings.getRole()
                    )

                    _uiState.update {
                        it.copy(isLoading = false, isRefreshing = false, profile = actualProfile)
                    }
                }
                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, isRefreshing = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private fun processLogout() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Eksekusi pembersihan total!
            // Fungsi ini (yang kita buat sebelumnya) akan menghapus token di UserSettings
            // sekaligus membersihkan tabel Events, Tickets, dan ScanLogs di SQLite.
            authRepository.performSecureLogout()

            // 2. Beri jeda sedikit agar animasi loading terasa natural dan memberi waktu proses I/O selesai
            delay(500)

            // 3. Ubah state untuk memicu LaunchedEffect di UI
            _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
        }
    }
}