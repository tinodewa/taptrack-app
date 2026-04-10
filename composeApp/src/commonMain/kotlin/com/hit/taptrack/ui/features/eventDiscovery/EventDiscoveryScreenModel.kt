package com.hit.taptrack.ui.features.eventDiscovery

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.model.EventModel
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.ui.features.eventDiscovery.states.EventDiscoveryEvent
import com.hit.taptrack.ui.features.eventDiscovery.states.EventDiscoveryUiState
import com.hit.taptrack.utils.helpers.UserSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDiscoveryScreenModel(
    private val tapTrackRepository: TapTrackRepository,
    private val userSettings: UserSettings // Diperlukan untuk mengecek role (Committee/Racer)
) : ScreenModel {

    private val _uiState = MutableStateFlow(EventDiscoveryUiState())
    val uiState: StateFlow<EventDiscoveryUiState> = _uiState.asStateFlow()

    init {
        // Cek Role untuk memunculkan/menyembunyikan tombol FAB "Buat Acara"
        val isCommitteeRole =
            userSettings.getRole() == "COMMITTEE" || userSettings.getRole() == "ADMIN"
        _uiState.update { it.copy(isCommittee = isCommitteeRole) }

        // Ambil data langsung saat layar pertama kali dibuka
//        fetchEvents()

        observeLocalEvents()
        refreshData()
    }

    fun onEvent(event: EventDiscoveryEvent) {
        when (event) {
            is EventDiscoveryEvent.TabSelected -> {
                _uiState.update { it.copy(selectedTabIndex = event.index) }
                applyFilter()
            }

            EventDiscoveryEvent.RefreshEvents -> refreshData()
        }
    }

    private fun observeLocalEvents() {
        screenModelScope.launch {
            tapTrackRepository.eventsFlow.collect { entities ->
                val mapped = entities.map { entity ->
                    EventModel(
                        id = entity.id,
                        name = entity.title,
                        date = entity.startDatetime,
                        location = "Sirkuit TapTrack", // Fallback: API belum mengirim lokasi
                        status = mapStringToStatus(entity.status)
                    )
                }
                _uiState.update { it.copy(allEvents = mapped, isLoading = false) }
                applyFilter()
            }
        }
    }

    fun refreshData() {
        screenModelScope.launch {
            // Kita tetap set loading jika DB masih kosong
            if (_uiState.value.allEvents.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }

            val result = tapTrackRepository.syncEvents()

            if (result is ResultState.Error) {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

//    private fun fetchEvents() {
//        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
//
//        screenModelScope.launch {
//            val result = tapTrackRepository.getEvents()
//
//            when (result) {
//                is ResultState.Success -> {
//                    // Mapping DTO (Events) menjadi UI Model (EventModel)
//                    val mappedEvents = result.data.map { dto ->
//                        EventModel(
//                            id = dto.id ?: "",
//                            name = dto.title ?: "Acara Tanpa Nama",
//                            date = dto.startDatetime ?: "Tanggal Belum Ditentukan",
//                            location = "Sirkuit TapTrack", // Fallback: API belum mengirim lokasi
//                            status = mapStringToStatus(dto.status)
//                        )
//                    }
//
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            allEvents = mappedEvents
//                        )
//                    }
//
//                    // Jangan lupa aplikasikan filter tab setelah data masuk
//                    applyFilter()
//                }
//
//                is ResultState.Error -> {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            errorMessage = result.message
//                        )
//                    }
//                }
//            }
//        }
//    }

    private fun applyFilter() {
        val currentState = _uiState.value
        val tabIndex = currentState.selectedTabIndex

        // Asumsi Tab: 0 = Semua, 1 = Mendatang (UPCOMING), 2 = Selesai (COMPLETED)
        val filtered = currentState.allEvents.filter { event ->
            when (tabIndex) {
                0 -> event.status == EventStatus.UPCOMING
                1 -> event.status == EventStatus.ONGOING
                2 -> event.status == EventStatus.COMPLETED
                else -> true
            }
        }

        _uiState.update { it.copy(filteredEvents = filtered) }
    }

    // Fungsi utilitas untuk menerjemahkan status string dari backend ke Enum UI kita
    private fun mapStringToStatus(status: String?): EventStatus {
        return when (status?.uppercase()) {
            "UPCOMING" -> EventStatus.UPCOMING
            "ONGOING", "LIVE" -> EventStatus.ONGOING
            "COMPLETED", "FINISHED" -> EventStatus.COMPLETED
            else -> EventStatus.UPCOMING // Default aman
        }
    }
}