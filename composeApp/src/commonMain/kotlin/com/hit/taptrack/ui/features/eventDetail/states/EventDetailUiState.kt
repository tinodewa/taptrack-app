package com.hit.taptrack.ui.features.eventDetail.states

import com.hit.taptrack.common.state.UserRole
import com.hit.taptrack.data.model.EventDetailModel

data class EventDetailUiState(
    val isLoading: Boolean = true,
    val eventDetail: EventDetailModel? = null,
    val userRole: UserRole = UserRole.RACER, // Default simulasi: Racer
    val isJoinLoading: Boolean = false,
    val isJoinSuccess: Boolean = false,
    val errorMessage: String? = null
)