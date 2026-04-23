package com.hit.taptrack.ui.features.eventDetail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.common.state.UserRole
import com.hit.taptrack.data.model.CategoryUiModel
import com.hit.taptrack.data.model.EventDetailModel
import com.hit.taptrack.data.model.StageUiModel
import com.hit.taptrack.data.model.TicketModel
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailEvent
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailUiState
import com.hit.taptrack.utils.helpers.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailScreenModel(
    private val eventId: String,
    private val tapTrackRepository: TapTrackRepository,
    private val userSettings: UserSettings
) : ScreenModel {

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    init {
        // Ambil role dari UserSettings dan petakan ke Enum
        val roleString = userSettings.getRole()
        val mappedRole = when (roleString.uppercase()) {
            "COMMITTEE", "ADMIN" -> UserRole.COMMITTEE
            else -> UserRole.RACER // Default aman
        }

        _uiState.update { it.copy(userRole = mappedRole) }

        // Mulai pemantauan lokal dan sinkronisasi jaringan
        observeEventDetail()
        syncEventDetailFromApi()
        observeLocalTicket() // FITUR BARU: Pantau tiket lokal!
    }

    fun onEvent(event: EventDetailEvent) {
        when (event) {
            EventDetailEvent.RefreshData -> syncEventDetailFromApi(isRefresh = true)
            EventDetailEvent.ShowJoinDialog -> _uiState.update {
                it.copy(
                    showJoinDialog = true,
                    raceNumberInput = "",
                    selectedCategoryId = null
                )
            }

            EventDetailEvent.HideJoinDialog -> _uiState.update { it.copy(showJoinDialog = false) }
            is EventDetailEvent.RaceNumberChanged -> _uiState.update { it.copy(raceNumberInput = event.number) }
            is EventDetailEvent.CategorySelected -> _uiState.update { it.copy(selectedCategoryId = event.categoryId) }
            EventDetailEvent.SubmitJoinEvent -> processJoinEvent()
            EventDetailEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            EventDetailEvent.ManageEvent -> {
                // TODO: Navigasi/Trigger ke fitur Kelola Acara (Committee Only)
            }
            EventDetailEvent.ShowTicketDialog -> _uiState.update { it.copy(showTicketDialog = true) }
            EventDetailEvent.HideTicketDialog -> _uiState.update { it.copy(showTicketDialog = false) }
        }
    }

    private fun observeLocalTicket() {
        screenModelScope.launch {
            // ASUMSI: Kamu memiliki fungsi di repository untuk membaca tiket berdasarkan eventId.
            // Flow ini akan otomatis ter-update jika processJoinEvent berhasil menyimpan tiket ke Room DB.
            tapTrackRepository.getTicketByEventIdFlow(eventId).collect { ticketEntity ->
                if (ticketEntity != null) {
                    _uiState.update {
                        it.copy(
                            isRegistered = true, // Ubah status menjadi terdaftar
                            ticketData = TicketModel(
                                registrationId = ticketEntity.eventId,
                                eventName = ticketEntity.title,
                                date = ticketEntity.startDatetime,
                                location = "Sirkuit Utama", // Fallback UI
                                category = "Kelas Umum", // Fallback UI
                                racerNumber = ticketEntity.raceNumber,
                                qrCodePayload = ticketEntity.qrTokenHash,
                                status = ticketEntity.registrationStatus
                            )
                        )
                    }
                } else {
                    _uiState.update { it.copy(isRegistered = false, ticketData = null) }
                }
            }
        }
    }

    private fun observeEventDetail() {
        screenModelScope.launch {
            // Gabungkan 3 aliran data dari Room Database
            combine(
                tapTrackRepository.getEventDetailFlow(eventId),
                tapTrackRepository.getCheckpointsFlow(eventId),
                tapTrackRepository.getCategoriesFlow(eventId)
            ) { eventEntity, checkpointEntities, categoryEntities ->

                // Jika data acara utama belum ada, kembalikan null
                if (eventEntity == null) return@combine null

                // 1. Petakan Categories
                val mappedCategories = categoryEntities.map { CategoryUiModel(it.id, it.categoryName) }

                // 2. Petakan Checkpoints menjadi Stages (Format Enduro)
                val groupedStages = checkpointEntities
                    .groupBy { it.stageNumber } // Kelompokkan berdasarkan SS
                    .entries // AMBIL PASANGAN KEY-VALUE
                    .sortedBy { it.key } // URUTKAN BERDASARKAN KEY (stageNumber)
                    .map { (stageNum, cps) ->
                        // Urutkan pos berdasarkan sequenceOrder (Start -> Middle -> Finish)
                        val sortedCps = cps
                            .sortedBy { it.sequenceOrder }
                            .map { it.checkpointName }

                        StageUiModel(
                            stageNumber = stageNum,
                            checkpoints = sortedCps
                        )
                    }

                // 3. Rakit menjadi satu Model UI yang utuh
                EventDetailModel(
                    id = eventEntity.id,
                    name = eventEntity.title,
                    description = eventEntity.descriptionText,
                    date = eventEntity.startDatetime,
                    status = mapStringToStatus(eventEntity.status),
                    categories = mappedCategories,
                    stages = groupedStages
                )

            }.collect { mappedDetail ->
                // Hasil dari blok combine di atas akan ditangkap di sini
                if (mappedDetail != null) {
                    // Data utuh siap dirender UI!
                    _uiState.update { it.copy(isLoading = false, eventDetail = mappedDetail) }
                } else {
                    // Jika eventEntity masih null (data belum masuk Room)
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun syncEventDetailFromApi(isRefresh: Boolean = false) {
        screenModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            }
            // Jangan ubah isLoading menjadi true jika data sudah ada di lokal
            // agar layar tidak berkedip kosong saat me-refresh.
            if (_uiState.value.eventDetail == null && !isRefresh) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val result = tapTrackRepository.syncEventDetail(eventId)

            if (result is ResultState.Error) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = result.message) }
            } else {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }
    }

    private fun processJoinEvent() {
        val state = _uiState.value
        if (state.raceNumberInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nomor lambung tidak boleh kosong.") }
            return
        }

        if (state.selectedCategoryId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Silakan pilih kelas balapan.") }
            return
        }

        _uiState.update { it.copy(isJoinLoading = true, errorMessage = null) }

        screenModelScope.launch {
            // Tembak Repository dengan parameter lengkap
            val result = tapTrackRepository.joinEvent(
                eventId = eventId,
                raceNumber = state.raceNumberInput,
                categoryId = state.selectedCategoryId
            )

            when (result) {
                is ResultState.Success -> {
                    // Sukses mendaftar!
                    _uiState.update {
                        it.copy(
                            isJoinLoading = false,
                            showJoinDialog = false,
                            isJoinSuccess = true,
                            // Pesan dari server, cth: "Registration successful"
                            joinSuccessMessage = result.data.message
                                ?: "Berhasil mendaftar balapan!"
                        )
                    }
                }

                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(
                            isJoinLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun mapStringToStatus(status: String?): EventStatus {
        return when (status?.uppercase()) {
            "UPCOMING" -> EventStatus.UPCOMING
            "ACTIVE", "LIVE" -> EventStatus.ACTIVE
            "COMPLETED", "FINISHED" -> EventStatus.FINISHED
            else -> EventStatus.UPCOMING
        }
    }
}