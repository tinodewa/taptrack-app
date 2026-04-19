package com.hit.taptrack.ui.features.eventCreate.states

// Model khusus UI untuk membungkus Stage
data class StageInputModel(
    val stageNumber: Int,
    val checkpoints: List<String>
)