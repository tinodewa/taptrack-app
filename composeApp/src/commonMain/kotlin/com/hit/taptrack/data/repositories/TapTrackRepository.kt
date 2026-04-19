package com.hit.taptrack.data.repositories

import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.sources.local.dao.CategoryDao
import com.hit.taptrack.data.sources.local.dao.CheckpointDao
import com.hit.taptrack.data.sources.local.dao.EventDao
import com.hit.taptrack.data.sources.local.dao.ParticipantDao
import com.hit.taptrack.data.sources.local.dao.ScanLogDao
import com.hit.taptrack.data.sources.local.dao.TicketDao
import com.hit.taptrack.data.sources.local.entity.CategoryEntity
import com.hit.taptrack.data.sources.local.entity.CheckpointEntity
import com.hit.taptrack.data.sources.local.entity.EventEntity
import com.hit.taptrack.data.sources.local.entity.ParticipantEntity
import com.hit.taptrack.data.sources.local.entity.ScanLogEntity
import com.hit.taptrack.data.sources.local.entity.TicketEntity
import com.hit.taptrack.data.sources.remote.api.TapTrackApi
import com.hit.taptrack.data.sources.remote.requestBody.ChangeEventStatusRequest
import com.hit.taptrack.data.sources.remote.requestBody.ChangeParticipantStatusRequest
import com.hit.taptrack.data.sources.remote.requestBody.CreateEventRequest
import com.hit.taptrack.data.sources.remote.requestBody.EventRegistrationRequest
import com.hit.taptrack.data.sources.remote.requestBody.ScanQrRequest
import com.hit.taptrack.data.sources.remote.responseBody.CreateEventResponse
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TapTrackRepository(
    private val tapTrackApi: TapTrackApi,
    private val userSettings: UserSettings,
    private val eventDao: EventDao,
    private val ticketDao: TicketDao,
    private val scanLogDao: ScanLogDao,
    private val participantDao: ParticipantDao,
    private val checkpointDao: CheckpointDao,
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    // 1. UI akan mengobservasi Flow ini. Setiap ada perubahan di DB, UI otomatis update.
    val eventsFlow: Flow<List<EventEntity>> = eventDao.getAllEvents()

    // 1. Ekspos Flow ke UI
    val myTicketsFlow: Flow<List<TicketEntity>> = ticketDao.getAllTickets()

    // Flow untuk dibaca oleh UI secara luring
    fun getCheckpointsFlow(eventId: String): Flow<List<CheckpointEntity>> =
        checkpointDao.getCheckpointsByEvent(eventId)

    // Observe participants for a specific event
    fun getParticipantsFlow(eventId: String): Flow<List<ParticipantEntity>> =
        participantDao.getParticipantsByEvent(eventId)

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
                            status = dto.status ?: "UPCOMING",
                            descriptionText = "",
                            checkpointCount = 0
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

    // 1. Jalur UI: Membaca secara real-time dari lokal
    fun getEventDetailFlow(eventId: String): Flow<EventEntity?> {
        return eventDao.getEventByIdFlow(eventId)
    }

    // 2. Jalur Jaringan: Menarik dari Golang dan menimpa data lokal
    suspend fun syncEventDetail(eventId: String): ResultState<Unit> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getEventDetail(eventId) },
                tag = "Repo_SyncEventDetail"
            )

            when (result) {
                is ResultState.Success -> {
                    val dto = result.data.data ?: return@withContext ResultState.Error(404, "Data kosong")

                    // 1. Simpan Acara ke tabel `events`
                    val eventEntity = EventEntity(
                        id = dto.id ?: "",
                        title = dto.title ?: "",
                        startDatetime = dto.startDatetime ?: "",
                        status = dto.status ?: "UPCOMING",
                        descriptionText = dto.descriptionText ?: "",
                        checkpointCount = dto.checkpoints.size
                    )
                    eventDao.upsertEvents(listOf(eventEntity))

                    // 2. Simpan Checkpoints ke tabel `checkpoints`
                    val checkpointEntities = dto.checkpoints.map { cp ->
                        CheckpointEntity(
                            id = cp.id ?: "",
                            eventId = dto.id ?: "",
                            checkpointName = cp.checkpointName ?: "",
                            typeFlag = cp.typeFlag ?: "MIDDLE",
                            sequenceOrder = cp.sequenceOrder ?: 0,
                            stageNumber = cp.stageNumber ?: 1
                        )
                    }
                    checkpointDao.upsertCheckpoints(checkpointEntities)

                    // 3. Simpan Categories ke tabel `categories` (FITUR BARU)
                    val categoryEntities = dto.categories.map { cat ->
                        CategoryEntity(
                            id = cat.id ?: "",
                            eventId = dto.id ?: "",
                            categoryName = cat.categoryName ?: "Umum"
                        )
                    }
                    categoryDao.upsertCategories(categoryEntities)

                    ResultState.Success(Unit)
                }
                is ResultState.Error -> ResultState.Error(result.statusCode, result.message)
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

    // 2. Fungsi Sinkronisasi Tiket (API ke Local DB)
    suspend fun syncMyTickets(): ResultState<Unit> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getRacerDataParticipantOnEvents() },
                tag = "Repo_SyncMyTickets"
            )

            when (result) {
                is ResultState.Success -> {
                    val dtoList = result.data.data ?: emptyList()

                    // Map dari API DTO ke SQLite Entity
                    val entities = dtoList.map { dto ->
                        TicketEntity(
                            eventId = dto.eventId ?: "UNKNOWN_ID",
                            title = dto.title ?: "Acara Tanpa Nama",
                            startDatetime = dto.startDatetime ?: "TBA",
                            raceNumber = dto.raceNumber ?: "-",
                            registrationStatus = dto.registrationStatus ?: "REGISTERED",
                            qrTokenHash = dto.qrTokenHash ?: ""
                        )
                    }

                    // Simpan secara lokal
                    ticketDao.upsertTickets(entities)
                    ResultState.Success(Unit)
                }

                is ResultState.Error -> {
                    ResultState.Error(result.statusCode, result.message)
                }
            }
        }
    }

    // Pastikan ScanLogDao sudah di-inject via konstruktor Repository
    suspend fun scanQrOfflineFirst(
        eventId: String,
        request: ScanQrRequest
    ): ResultState<ScanQrResponse> {
        return withContext(ioDispatcher) {

            // 1. Buat ID lokal yang unik
            val localId = "${request.absoluteTimestamp}_${request.qrTokenHash}"

            // 2. Simpan ke Room dengan status PENDING
            val entity = ScanLogEntity(
                localLogId = localId,
                eventId = eventId,
                qrTokenHash = request.qrTokenHash ?: "",
                checkpointId = request.checkpointId ?: "",
                absoluteTimestamp = request.absoluteTimestamp ?: "",
                manualOverrideFlag = request.manualOverrideFlag.toString(),
                syncStatus = "PENDING"
            )
            scanLogDao.insertLog(entity)

            // 3. Tembak API secara langsung
            val result = handleApiCall(
                apiCall = { tapTrackApi.scanQr(request) },
                tag = "Repo_ScanQR_OfflineFirst"
            )

            // 4. Evaluasi Hasil API
            when (result) {
                is ResultState.Success -> {
                    // Sinyal bagus & berhasil! Ubah status lokal jadi SYNCED
                    scanLogDao.updateSyncStatus(localId, "SYNCED")
                    result
                }

                is ResultState.Error -> {
                    // Di sini kita membedakan Error Jaringan vs Error Data
                    // Asumsi: Kode 0 atau -1 biasanya karena gagal konek (No Internet/Timeout)
                    // Kode 400-499 biasanya karena QR palsu/salah.

                    if (result.statusCode == 0 || result.statusCode == -1 || result.statusCode!! >= 500) {
                        // INTERNET MATI: Kita kembalikan "Success Semu" agar Panitia bisa lanjut kerja
                        ResultState.Success(
                            ScanQrResponse(
                                message = "Tersimpan Lokal (Menunggu Sinyal)",
                                syncStatus = "PENDING"
                            )
                        )
                    } else {
                        // QR CODE TIDAK VALID DARI SERVER: Kita tandai FAILED di lokal
                        scanLogDao.updateSyncStatus(localId, "FAILED")
                        result // Tetap kembalikan Error agar UI muncul warna merah
                    }
                }
            }
        }
    }

    suspend fun syncPendingScans(): ResultState<Int> {
        return withContext(ioDispatcher) {
            // 1. Ambil semua log yang masih PENDING
            val pendingLogs = scanLogDao.getPendingScans()
            if (pendingLogs.isEmpty()) return@withContext ResultState.Success(0)

            var successCount = 0

            // 2. Iterasi dan kirim ke server
            pendingLogs.forEach { log ->
                val request = ScanQrRequest(
                    qrTokenHash = log.qrTokenHash,
                    checkpointId = log.checkpointId,
                    absoluteTimestamp = log.absoluteTimestamp,
                    manualOverrideFlag = log.manualOverrideFlag.toBoolean()
                )

                val result = handleApiCall(
                    apiCall = { tapTrackApi.scanQr(request) },
                    tag = "Repo_Sync_Batch"
                )

                if (result is ResultState.Success) {
                    // Update status di lokal agar tidak dikirim ulang
                    scanLogDao.updateSyncStatus(log.localLogId, "SYNCED")
                    successCount++
                }
            }

            ResultState.Success(successCount)
        }
    }

    suspend fun overrideParticipantStatus(
        registrationId: String,
        status: String
    ): ResultState<OverrideParticipantStatusResponse> {
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

    suspend fun updateEventStatus(eventId: String, newStatus: String): ResultState<Unit> {
        return withContext(ioDispatcher) {
            // 1. Siapkan Request Body
            val request = ChangeEventStatusRequest(status = newStatus)

            // 2. Tembak API
            val result = handleApiCall(
                apiCall = { tapTrackApi.updateEventStatus(eventId, request) },
                tag = "Repo_UpdateEventStatus"
            )

            when (result) {
                is ResultState.Success -> {
                    // 3. Jika API sukses, ubah juga status di database lokal
                    // Asumsi: eventDao sudah di-inject ke Repository ini
                    eventDao.updateEventStatusLocal(eventId, newStatus)

                    ResultState.Success(Unit)
                }

                is ResultState.Error -> {
                    ResultState.Error(result.statusCode, result.message)
                }
            }
        }
    }

    suspend fun syncParticipants(eventId: String): ResultState<Unit> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getEventParticipantList(eventId) },
                tag = "Repo_SyncParticipants"
            )

            when (result) {
                is ResultState.Success -> {
                    val dtoList = result.data.data
                    val entities = dtoList.map { dto ->
                        ParticipantEntity(
                            registrationId = dto.registrationId ?: "",
                            eventId = eventId,
                            raceNumber = dto.raceNumber ?: "-",
                            racerName = dto.racerName ?: "Unknown",
                            vehicleClass = dto.vehicleClass ?: "-",
                            registrationStatus = dto.registrationStatus ?: "REGISTERED",
                            lastCheckpoint = dto.lastCheckpoint
                        )
                    }
                    participantDao.upsertParticipants(entities)
                    ResultState.Success(Unit)
                }

                is ResultState.Error -> ResultState.Error(result.statusCode, result.message)
            }
        }
    }

    suspend fun createNewEvent(request: CreateEventRequest): ResultState<CreateEventResponse> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.createEvent(request) },
                tag = "Repo_CreateEvent"
            )

            when (result) {
                is ResultState.Success -> {
                    // Saat sukses, API biasanya mengembalikan ID Acara yang baru dibuat.
                    val newEventId = result.data.eventId ?: return@withContext ResultState.Error(
                        500,
                        "ID Acara tidak ditemukan dari server"
                    )
                    val newStatus = result.data.status ?: "UPCOMING"

                    // Langsung masukkan ke Room Database lokal!
                    val newEntity = EventEntity(
                        id = newEventId,
                        title = request.title ?: "Acara Baru",
                        startDatetime = request.startDatetime ?: "",
                        status = newStatus,
                        descriptionText = "",
                        checkpointCount = 0
                    )
                    eventDao.upsertEvents(listOf(newEntity))

                    ResultState.Success(result.data)
                }

                is ResultState.Error -> {
                    ResultState.Error(result.statusCode, result.message)
                }
            }
        }
    }

    suspend fun syncCheckpoints(eventId: String): ResultState<Unit> {
        return withContext(ioDispatcher) {
            val result = handleApiCall(
                apiCall = { tapTrackApi.getEventCheckpointByEventId(eventId) },
                tag = "Repo_SyncCheckpoints"
            )
            when (result) {
                is ResultState.Success -> {
                    val dtoList = result.data.data
                    val entities = dtoList.map { dto ->
                        CheckpointEntity(
                            id = dto.id ?: "",
                            eventId = eventId,
                            checkpointName = dto.checkpointName ?: "Unknown",
                            typeFlag = dto.typeFlag ?: "MIDDLE",
                            sequenceOrder = dto.sequenceOrder ?: 0,
                            stageNumber = dto.stageNumber ?: 1
                        )
                    }
                    checkpointDao.upsertCheckpoints(entities)
                    ResultState.Success(Unit)
                }

                is ResultState.Error -> ResultState.Error(result.statusCode, result.message)
            }
        }
    }
}