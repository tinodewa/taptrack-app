package com.hit.taptrack.ui.features.eventCreate

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateEvent
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventCreateScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(EventCreateUiState())
    val uiState: StateFlow<EventCreateUiState> = _uiState.asStateFlow()

    fun onEvent(event: EventCreateEvent) {
        when (event) {
            is EventCreateEvent.NameChanged -> _uiState.update { it.copy(name = event.name) }
            is EventCreateEvent.DateChanged -> _uiState.update { it.copy(date = event.date) }
            is EventCreateEvent.LocationChanged -> _uiState.update { it.copy(location = event.location) }
            is EventCreateEvent.DescriptionChanged -> _uiState.update { it.copy(description = event.desc) }
            is EventCreateEvent.RulesChanged -> _uiState.update { it.copy(rules = event.rules) }
            is EventCreateEvent.MaxCapacityChanged -> _uiState.update { it.copy(maxCapacity = event.capacity) }

            is EventCreateEvent.AddCategory -> {
                val current = _uiState.value.categories.toMutableList()
                current.add(event.category)
                _uiState.update { it.copy(categories = current) }
            }
            is EventCreateEvent.RemoveCategory -> {
                val current = _uiState.value.categories.toMutableList()
                if (current.size > 1) current.removeAt(event.index) // Minimal 1 kelas
                _uiState.update { it.copy(categories = current) }
            }

            is EventCreateEvent.AddCheckpoint -> {
                val current = _uiState.value.checkpoints.toMutableList()
                // Sisipkan sebelum elemen terakhir (Garis Finish)
                val insertIndex = if (current.size > 1) current.size - 1 else current.size
                current.add(insertIndex, event.name)
                _uiState.update { it.copy(checkpoints = current) }
            }
            is EventCreateEvent.RemoveCheckpoint -> {
                val current = _uiState.value.checkpoints.toMutableList()
                // Jangan izinkan menghapus index 0 (Start) atau index terakhir (Finish)
                if (event.index > 0 && event.index < current.size - 1) {
                    current.removeAt(event.index)
                    _uiState.update { it.copy(checkpoints = current) }
                }
            }
            is EventCreateEvent.UpdateCheckpoint -> {
                val current = _uiState.value.checkpoints.toMutableList()
                current[event.index] = event.newName
                _uiState.update { it.copy(checkpoints = current) }
            }

            EventCreateEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            EventCreateEvent.SubmitEvent -> submitEvent()
        }
    }

    private fun submitEvent() {
        val state = _uiState.value
        if (state.name.isBlank() || state.location.isBlank() || state.date.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama, Tanggal, dan Lokasi wajib diisi.") }
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // TODO: Integrasi dengan POST /api/v1/events
            delay(1500)
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }
}