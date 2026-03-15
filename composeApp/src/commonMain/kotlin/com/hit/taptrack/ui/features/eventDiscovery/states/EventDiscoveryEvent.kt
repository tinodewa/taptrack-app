package com.hit.taptrack.ui.features.eventDiscovery.states

sealed class EventDiscoveryEvent {
    data class TabSelected(val index: Int) : EventDiscoveryEvent()
    object RefreshEvents : EventDiscoveryEvent()
}