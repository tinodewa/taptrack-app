package com.hit.taptrack.data.model

// Model khusus UI untuk membungkus Stage
data class StageInputModel(
    val stageNumber: Int,
    val checkpoints: List<String>
)