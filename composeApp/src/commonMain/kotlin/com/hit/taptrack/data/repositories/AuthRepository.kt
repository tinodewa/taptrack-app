package com.hit.taptrack.data.repositories

import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.sources.local.dao.EventDao
import com.hit.taptrack.data.sources.local.dao.ScanLogDao
import com.hit.taptrack.data.sources.local.dao.TicketDao
import com.hit.taptrack.data.sources.remote.api.TapTrackApi
import com.hit.taptrack.data.sources.remote.requestBody.AuthRequest
import com.hit.taptrack.data.sources.remote.requestBody.UpdateProfileRequest
import com.hit.taptrack.data.sources.remote.responseBody.LoginResponse
import com.hit.taptrack.data.sources.remote.responseBody.Profile
import com.hit.taptrack.utils.helpers.UserSettings
import com.hit.taptrack.utils.helpers.handleApiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AuthRepository(
    private val tapTrackApi: TapTrackApi,
    private val userSettings: UserSettings,
    private val eventDao: EventDao,
    private val ticketDao: TicketDao,
    private val scanLogDao: ScanLogDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend fun login(request: AuthRequest): ResultState<LoginResponse> {
        return withContext(ioDispatcher) {

            // 1. Lakukan pemanggilan API melalui wrapper handleApiCall
            val result = handleApiCall(
                apiCall = { tapTrackApi.login(request) },
                tag = "TapTrackRepository_Login"
            )

            // 2. Jika sukses, simpan kredensial ke penyimpanan lokal
            if (result is ResultState.Success) {
                val body = result.data // Asumsi ResultState.Success memiliki properti 'data'

                if (!body.token.isNullOrBlank()) {
                    userSettings.saveAuthData(body.token!!, body.role, body.profileId)
                } else {
                    // Opsional: Handle jika server mengembalikan 200 OK tapi token kosong
                    return@withContext ResultState.Error(
                        statusCode = 200,
                        message = "Login berhasil, tetapi token tidak ditemukan dari server."
                    )
                }
            }

            // 3. Kembalikan ResultState (baik Success maupun Error) ke ViewModel
            result
        }
    }

    suspend fun registerAndSetupProfile(
        registerReq: AuthRequest,
        profileReq: UpdateProfileRequest
    ): ResultState<Unit> {
        return withContext(ioDispatcher) {

            // 1. TEMBAK API REGISTER
            val registerResult = handleApiCall(
                apiCall = { tapTrackApi.register(registerReq) },
                tag = "Repo_Register"
            )
            if (registerResult is ResultState.Error) {
                return@withContext ResultState.Error(
                    registerResult.statusCode,
                    registerResult.message
                )
            }

            // 2. TEMBAK API LOGIN (Untuk mendapatkan Token JWT)
            val loginResult = handleApiCall(
                apiCall = {
                    tapTrackApi.login(
                        AuthRequest(
                            registerReq.email,
                            registerReq.password
                        )
                    )
                },
                tag = "Repo_Login_After_Register"
            )

            when (loginResult) {
                is ResultState.Error -> {
                    return@withContext ResultState.Error(
                        loginResult.statusCode,
                        "Registrasi berhasil, tapi gagal masuk secara otomatis."
                    )
                }

                is ResultState.Success -> {
                    val body = loginResult.data
                    if (body.token.isNullOrBlank()) {
                        return@withContext ResultState.Error(200, "Token tidak valid dari server.")
                    }

                    // SIMPAN TOKEN KE PENYIMPANAN LOKAL
                    // (Ini krusial agar request Update Profile di bawah bisa berjalan!)
                    userSettings.saveAuthData(
                        token = body.token!!,
                        role = body.role ?: "RACER",
                        profileId = body.profileId
                    )
                }
            }

            // 3. TEMBAK API UPDATE PROFILE
            val profileResult = handleApiCall(
                apiCall = { tapTrackApi.updateProfile(profileReq) },
                tag = "Repo_Update_Profile"
            )

            if (profileResult is ResultState.Error) {
                // Jika gagal update profil, token sudah telanjur tersimpan.
                // Kamu bisa memilih menghapus token lagi (rollback) atau membiarkannya.
                // Di sini kita biarkan masuk, tapi beri tahu gagal update profil.
                return@withContext ResultState.Error(
                    profileResult.statusCode,
                    "Akun terbuat, tapi gagal menyimpan data profil: ${profileResult.message}"
                )
            }

            // JIKA KETIGANYA SUKSES
            ResultState.Success(Unit)
        }
    }

    suspend fun getProfile(): ResultState<Profile> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getProfile() },
                tag = "Repo_GetProfile"
            )

            when (result) {
                is ResultState.Success -> {
                    val profileData = result.data.data
                    if (profileData != null) {
                        ResultState.Success(profileData)
                    } else {
                        ResultState.Error(404, "Data profil tidak ditemukan.")
                    }
                }
                is ResultState.Error -> {
                    ResultState.Error(result.statusCode, result.message)
                }
            }
        }
    }

    // Pastikan UserSettings, EventDao, TicketDao, dan ScanLogDao sudah di-inject
    suspend fun performSecureLogout() {
        withContext(ioDispatcher) {
            // 1. Sapu bersih semua data SQLite secara serentak
            // Gunakan coroutine launch agar berjalan paralel dan lebih cepat
            launch { eventDao.clearAllEvents() }
            launch { ticketDao.clearAllTickets() }
            launch { scanLogDao.clearAllLogs() }

            // 2. Hapus Token, Role, dan Data Sesi dari K-V Storage (Multiplatform Settings)
            // (Asumsi kamu memiliki fungsi clear() atau clearAuthData() di class UserSettings)
            userSettings.clearAuthData()
        }
    }
}