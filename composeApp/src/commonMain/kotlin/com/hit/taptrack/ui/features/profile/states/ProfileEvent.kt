package com.hit.taptrack.ui.features.profile.states

sealed class ProfileEvent {
    object LogoutClicked : ProfileEvent()
    object RefreshProfile : ProfileEvent()
}