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

    private fun refreshData() {
        screenModelScope.launch {
            // Nyalakan indikator refreshing (animasi muter di atas layar)
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

            // Jika DB benar-benar kosong, baru kita nyalakan loading tengah
            if (_uiState.value.allEvents.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }

            // Panggil API (Background Sync)
            val result = tapTrackRepository.syncEvents()

            if (result is ResultState.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }

            // Matikan SEMUA indikator loading
            // (observeLocalEvents akan otomatis memperbarui list jika ada data baru)
            _uiState.update { it.copy(isRefreshing = false, isLoading = false) }
        }
    }

    private fun applyFilter() {
        val currentState = _uiState.value
        val tabIndex = currentState.selectedTabIndex

        // Asumsi Tab: 0 = Semua, 1 = Mendatang (UPCOMING), 2 = Selesai (COMPLETED)
        val filtered = currentState.allEvents.filter { event ->
            when (tabIndex) {
                0 -> event.status == EventStatus.UPCOMING
                1 -> event.status == EventStatus.ACTIVE
                2 -> event.status == EventStatus.FINISHED
                else -> true
            }
        }

        _uiState.update { it.copy(filteredEvents = filtered) }
    }

    // Fungsi utilitas untuk menerjemahkan status string dari backend ke Enum UI kita
    private fun mapStringToStatus(status: String?): EventStatus {
        return when (status?.uppercase()) {
            "UPCOMING" -> EventStatus.UPCOMING
            "ONGOING", "LIVE", "ACTIVE" -> EventStatus.ACTIVE
            "COMPLETED", "FINISHED" -> EventStatus.FINISHED
            else -> EventStatus.UPCOMING // Default aman
        }
    }
}