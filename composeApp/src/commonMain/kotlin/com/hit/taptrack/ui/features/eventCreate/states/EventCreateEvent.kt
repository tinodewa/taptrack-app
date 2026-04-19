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

    // --- FITUR BARU: MANAJEMEN SPECIAL STAGE (SS) ---
    object AddStage : EventCreateEvent()
    data class RemoveStage(val stageIndex: Int) : EventCreateEvent()
    data class AddCheckpointToStage(val stageIndex: Int) : EventCreateEvent()
    data class RemoveCheckpointFromStage(val stageIndex: Int, val cpIndex: Int) : EventCreateEvent()
    data class UpdateCheckpointName(val stageIndex: Int, val cpIndex: Int, val newName: String) : EventCreateEvent()

    object SubmitEvent : EventCreateEvent()
    object ClearError : EventCreateEvent()
}