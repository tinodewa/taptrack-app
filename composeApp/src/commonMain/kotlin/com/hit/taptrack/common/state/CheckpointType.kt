package com.hit.taptrack.common.state

// Jenis titik pindai di lintasan
enum class CheckpointType(val displayName: String) {
    START("Garis Start"),
    CHECKPOINT_1("Pos 1 (Sektor A)"),
    FINISH("Garis Finish")
}