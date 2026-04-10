package com.hit.taptrack.data.sources.remote.api

import com.hit.taptrack.data.sources.remote.requestBody.AuthRequest
import com.hit.taptrack.data.sources.remote.requestBody.ChangeEventStatusRequest
import com.hit.taptrack.data.sources.remote.requestBody.ChangeParticipantStatusRequest
import com.hit.taptrack.data.sources.remote.requestBody.CreateEventRequest
import com.hit.taptrack.data.sources.remote.requestBody.EventRegistrationRequest
import com.hit.taptrack.data.sources.remote.requestBody.ScanQrRequest
import com.hit.taptrack.data.sources.remote.requestBody.UpdateProfileRequest
import com.hit.taptrack.data.sources.remote.responseBody.CreateEventResponse
import com.hit.taptrack.data.sources.remote.responseBody.EventCheckpointsResponse
import com.hit.taptrack.data.sources.remote.responseBody.EventDetailResponse
import com.hit.taptrack.data.sources.remote.responseBody.EventParticipantListResponse
import com.hit.taptrack.data.sources.remote.responseBody.EventRegisterResponse
import com.hit.taptrack.data.sources.remote.responseBody.EventsParticipantResponse
import com.hit.taptrack.data.sources.remote.responseBody.EventsResponse
import com.hit.taptrack.data.sources.remote.responseBody.LeaderboardResponse
import com.hit.taptrack.data.sources.remote.responseBody.LoginResponse
import com.hit.taptrack.data.sources.remote.responseBody.OverrideParticipantStatusResponse
import com.hit.taptrack.data.sources.remote.responseBody.ProfileResponse
import com.hit.taptrack.data.sources.remote.responseBody.RegisterResponse
import com.hit.taptrack.data.sources.remote.responseBody.ScanQrResponse
import com.hit.taptrack.data.sources.remote.responseBody.SingleEventParticipantResponse
import com.hit.taptrack.data.sources.remote.responseBody.TestApiResponse
import com.hit.taptrack.data.sources.remote.responseBody.UpdateProfileResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

// --- INTERFACE API ---
interface TapTrackApi {

    // Test Endpoint
    @GET("api/health")
    suspend fun getHealth(): Response<TestApiResponse>

    // --- GLOBAL ---
    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Response<RegisterResponse>

    // Profile
    @GET("profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @POST("profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UpdateProfileResponse>

    // Leaderboard
    @GET("events/{event_id}/leaderboard")
    suspend fun getLeaderboardByEventId(@Path("event_id") eventId: String): Response<LeaderboardResponse>

    // Events
    @GET("events")
    suspend fun getEvents(): Response<EventsResponse>

    @GET("events/{event_id}")
    suspend fun getEventDetail(@Path("event_id") eventId: String): Response<EventDetailResponse>

    @GET("events/{event_id}/checkpoint")
    suspend fun getEventCheckpointByEventId(@Path("event_id") eventId: String): Response<EventCheckpointsResponse>

    // --- RACER ---
    // Registrations
    @POST("registrations")
    suspend fun joinEvent(@Body request: EventRegistrationRequest): Response<EventRegisterResponse>

    @GET("registrations/my-events")
    suspend fun getRacerDataParticipantOnEvents(): Response<EventsParticipantResponse>

    @GET("registrations/my-events/{event_id}")
    suspend fun getRacerDataParticipantByEventId(@Path("event_id") eventId: String): Response<SingleEventParticipantResponse>

    // --- COMMITTEE/ADMIN ---
    // Event Participant
    @GET("events/{event_id}/participants")
    suspend fun getEventParticipantList(@Path("event_id") eventId: String): Response<EventParticipantListResponse>

    // Create Event
    @POST("events")
    suspend fun createEvent(@Body request: CreateEventRequest): Response<CreateEventResponse>

    // Edit Event Status
    @PUT("events/{event_id}/status")
    suspend fun updateEventStatus(
        @Path("event_id") eventId: String,
        @Body request: ChangeEventStatusRequest
    ): Response<CreateEventResponse>

    // Override Participant Status
    @PUT("registrations/override/{registration_id}/status")
    suspend fun overrideParticipantStatus(
        @Path("registration_id") registrationId: String,
        @Body request: ChangeParticipantStatusRequest
    ): Response<OverrideParticipantStatusResponse>

    // Scans QR
    @POST("scans")
    suspend fun scanQr(@Body request: ScanQrRequest): Response<ScanQrResponse>

}