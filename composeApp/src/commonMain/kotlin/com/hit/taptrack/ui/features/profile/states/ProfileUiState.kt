package com.hit.taptrack.ui.features.profile.states

import com.hit.taptrack.data.model.UserProfileModel

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfileModel? = null,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null
)