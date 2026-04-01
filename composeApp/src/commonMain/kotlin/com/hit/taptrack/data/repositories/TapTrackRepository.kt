package com.hit.taptrack.data.repositories

import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.sources.remote.api.TapTrackApi
import com.hit.taptrack.data.sources.remote.requestBody.AuthRequest
import com.hit.taptrack.data.sources.remote.responseBody.EventsResponse
import com.hit.taptrack.data.sources.remote.responseBody.LoginResponse
import com.hit.taptrack.data.sources.remote.responseBody.TestApiResponse
import com.hit.taptrack.utils.helpers.UserSettings
import com.hit.taptrack.utils.helpers.handleApiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TapTrackRepository(
    private val tapTrackApi: TapTrackApi,
    private val userSettings: UserSettings,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend fun getHealth(): ResultState<TestApiResponse> = withContext(ioDispatcher) {
        handleApiCall(
            {
                tapTrackApi.getHealth()
            },
            "getHealth"
        )
    }

    suspend fun getEvents(): ResultState<List<EventsResponse>> = withContext(ioDispatcher) {
        handleApiCall(
            {
                tapTrackApi.getEvents()
            },
            "getEvents"
        )
    }

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
}