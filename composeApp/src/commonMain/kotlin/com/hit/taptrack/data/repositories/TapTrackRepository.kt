package com.hit.taptrack.data.repositories

import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.sources.remote.api.TapTrackApi
import com.hit.taptrack.data.sources.remote.responseBody.EventsResponse
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
}