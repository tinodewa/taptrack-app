package com.hit.taptrack.ui.features.eventCreate.states


sealed class EventCreateEvent {
    data class NameChanged(val name: String) : EventCreateEvent()
    data class DateChanged(val date: String) : EventCreateEvent()
    data class LocationChanged(val location: String) : EventCreateEvent()
    data class DescriptionChanged(val desc: String) : EventCreateEvent()
    data class RulesChanged(val rules: String) : EventCreateEvent()
    data class MaxCapacityChanged(val capacity: String) : EventCreateEvent()

    // List Dinamis
    data class AddCategory(val category: String) : EventCreateEvent()
    data class RemoveCategory(val index: Int) : EventCreateEvent()

    data class AddCheckpoint(val name: String) : EventCreateEvent()
    data class RemoveCheckpoint(val index: Int) : EventCreateEvent()
    data class UpdateCheckpoint(val index: Int, val newName: String) : EventCreateEvent()

    object SubmitEvent : EventCreateEvent()
    object ClearError : EventCreateEvent()
}