package com.hit.taptrack.common.state

enum class EventStatus {
    UPCOMING, // Acara belum dimulai
    ACTIVE,   // Acara sedang berlangsung (Pembalap bisa dipindai QR-nya)
    FINISHED, // Acara telah usai
}