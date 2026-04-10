package com.hit.taptrack.data.repositories

import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.sources.local.dao.EventDao
import com.hit.taptrack.data.sources.local.entity.EventEntity
import com.hit.taptrack.data.sources.remote.api.TapTrackApi
import com.hit.taptrack.data.sources.remote.requestBody.ChangeParticipantStatusRequest
import com.hit.taptrack.data.sources.remote.requestBody.EventRegistrationRequest
import com.hit.taptrack.data.sources.remote.requestBody.ScanQrRequest
import com.hit.taptrack.data.sources.remote.responseBody.EventDetail
import com.hit.taptrack.data.sources.remote.responseBody.EventRegisterResponse
import com.hit.taptrack.data.sources.remote.responseBody.Events
import com.hit.taptrack.data.sources.remote.responseBody.EventsParticipation
import com.hit.taptrack.data.sources.remote.responseBody.LeaderboardResponse
import com.hit.taptrack.data.sources.remote.responseBody.OverrideParticipantStatusResponse
import com.hit.taptrack.data.sources.remote.responseBody.ScanQrResponse
import com.hit.taptrack.utils.helpers.UserSettings
import com.hit.taptrack.utils.helpers.handleApiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TapTrackRepository(
    private val tapTrackApi: TapTrackApi,
    private val userSettings: UserSettings,
    private val eventDao: EventDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    // 1. UI akan mengobservasi Flow ini. Setiap ada perubahan di DB, UI otomatis update.
    val eventsFlow: Flow<List<EventEntity>> = eventDao.getAllEvents()

    // 2. Fungsi untuk sinkronisasi data dari API ke Local DB
    suspend fun syncEvents(): ResultState<Unit> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getEvents() },
                tag = "Repo_SyncEvents"
            )

            when (result) {
                is ResultState.Success -> {
                    val dtoList = result.data.data

                    // Petakan dari DTO API ke Entity Room
                    val entities = dtoList.map { dto ->
                        EventEntity(
                            id = dto.id ?: "",
                            title = dto.title ?: "TBA",
                            startDatetime = dto.startDatetime ?: "",
                            status = dto.status ?: "UPCOMING"
                        )
                    }

                    // Simpan ke Room (Upsert akan mengupdate jika ID sudah ada)
                    eventDao.upsertEvents(entities)
                    ResultState.Success(Unit)
                }
                is ResultState.Error -> {
                    ResultState.Error(result.statusCode, result.message)
                }
            }
        }
    }

    suspend fun getEvents(): ResultState<List<Events>> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getEvents() },
                tag = "Repo_GetEvents"
            )

            when (result) {
                is ResultState.Success -> {
                    // Bongkar EventsResponse, ambil array 'data' di dalamnya
                    // Jika null, kembalikan list kosong sebagai fallback yang aman
                    val eventsList = result.data.data
                    ResultState.Success(eventsList)
                }
                is ResultState.Error -> {
                    ResultState.Error(result.statusCode, result.message)
                }
            }
        }
    }

    suspend fun getEventDetail(eventId: String): ResultState<EventDetail> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getEventDetail(eventId) },
                tag = "Repo_GetEventDetail"
            )

            when (result) {
                is ResultState.Success -> {
                    // Bongkar Response, pastikan objek data tidak null
                    val detail = result.data.data
                    if (detail != null) {
                        ResultState.Success(detail)
                    } else {
                        ResultState.Error(404, "Data detail acara tidak ditemukan.")
                    }
                }
                is ResultState.Error -> {
                    ResultState.Error(result.statusCode, result.message)
                }
            }
        }
    }
    suspend fun joinEvent(eventId: String, raceNumber: String): ResultState<EventRegisterResponse> {
        return withContext(ioDispatcher) {
            val request = EventRegistrationRequest(
                eventId = eventId,
                raceNumber = raceNumber
            )

            handleApiCall(
                apiCall = { tapTrackApi.joinEvent(request) },
                tag = "Repo_JoinEvent"
            )
        }
    }

    suspend fun getMyTickets(): ResultState<List<EventsParticipation>> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getRacerDataParticipantOnEvents() },
                tag = "Repo_GetMyTickets"
            )

            when (result) {
                is ResultState.Success -> {
                    // Ambil array 'data', berikan list kosong jika null
                    val ticketsList = result.data.data
                    ResultState.Success(ticketsList)
                }
                is ResultState.Error -> {
                    ResultState.Error(result.statusCode, result.message)
                }
            }
        }
    }

    suspend fun scanQr(request: ScanQrRequest): ResultState<ScanQrResponse> {
        return withContext(ioDispatcher) {
            handleApiCall(
                apiCall = { tapTrackApi.scanQr(request) },
                tag = "Repo_ScanQr"
            )
        }
    }

    suspend fun overrideParticipantStatus(registrationId: String, status: String): ResultState<OverrideParticipantStatusResponse> {
        return withContext(ioDispatcher) {
            val request = ChangeParticipantStatusRequest(status = status)
            handleApiCall(
                apiCall = { tapTrackApi.overrideParticipantStatus(registrationId, request) },
                tag = "Repo_OverrideStatus"
            )
        }
    }

    suspend fun getLeaderboard(eventId: String): ResultState<LeaderboardResponse> {
        return withContext(ioDispatcher) {
            handleApiCall(
                apiCall = { tapTrackApi.getLeaderboardByEventId(eventId) },
                tag = "Repo_GetLeaderboard"
            )
        }
    }
}