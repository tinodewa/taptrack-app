package com.hit.taptrack.ui.features.eventDetail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.common.state.EventStatus
import com.hit.taptrack.common.state.UserRole
import com.hit.taptrack.data.model.EventDetailModel
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailEvent
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailScreenModel(private val eventId: String) : ScreenModel {

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    init {
        loadEventDetail()
    }

    private fun loadEventDetail() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulasi API GET /api/v1/events/:event_id

            // Dummy Data
            val dummyDetail = EventDetailModel(
                id = eventId,
                name = "Kejuaraan Daerah Motocross Jatim",
                description = "Seri pembuka kejuaraan daerah yang mempertemukan pembalap terbaik se-Jawa Timur di trek tanah ekstrem.",
                date = "15 April 2026",
                location = "Sirkuit Internasional Batu, Malang, Jawa Timur",
                rules = "Wajib menggunakan perlengkapan keselamatan standar FIM. Toleransi keterlambatan scrutineering maksimal 30 menit.",
                categories = listOf("Bebek Modif 4T 130cc", "Sport Trail 250cc Open", "FFA (Free For All)"),
                status = EventStatus.COMPLETED,
                registeredRacers = 120,
                maxCapacity = 200
            )

            // TODO: Ambil userRole dari penyimpanan lokal / sesi token (Multiplatform Settings)
            // Untuk saat ini kita simulasikan sebagai RACER. (Kamu bisa ubah ke COMMITTEE untuk melihat perbedaannya)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    eventDetail = dummyDetail,
                    userRole = UserRole.RACER // Ganti dengan userRole yang sesuai: RACER, COMMITTEE, dll.
                )
            }
        }
    }

    fun onEvent(event: EventDetailEvent) {
        when (event) {
            EventDetailEvent.JoinEvent -> processJoinEvent()
            EventDetailEvent.ManageEvent -> {
                // TODO: Navigasi/Trigger ke fitur Kelola Acara (Committee Only)
            }
            EventDetailEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun processJoinEvent() {
        screenModelScope.launch {
            _uiState.update { it.copy(isJoinLoading = true) }
            try {
                // TODO: Tembak API POST /api/v1/registrations dengan eventId
                delay(1500) // Simulasi network
                _uiState.update { it.copy(isJoinLoading = false, isJoinSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isJoinLoading = false, errorMessage = "Gagal mendaftar ke acara ini.") }
            }
        }
    }
}