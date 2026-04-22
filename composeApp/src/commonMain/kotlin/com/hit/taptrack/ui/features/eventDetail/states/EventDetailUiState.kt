package com.hit.taptrack.ui.features.eventDetail.states

import com.hit.taptrack.common.state.UserRole
import com.hit.taptrack.data.model.EventDetailModel

data class EventDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val eventDetail: EventDetailModel? = null,
    val userRole: UserRole = UserRole.RACER, // Default simulasi: Racer

    // --- State Pendaftaran ---
    val showJoinDialog: Boolean = false,
    val raceNumberInput: String = "",
    val selectedCategoryId: String? = null,
    val isJoinLoading: Boolean = false,
    val isJoinSuccess: Boolean = false,
    val joinSuccessMessage: String? = null,

    val errorMessage: String? = null
)