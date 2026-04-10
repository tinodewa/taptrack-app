package com.hit.taptrack.common.state

enum class ParticipantStatus {
    REGISTERED,
    RACING,     // Bisa juga dari status "ON_TRACK"
    FINISHED,   // Bisa juga dari status "COMPLETED"
    DNF,        // Did Not Finish (Gagal Finis)
    DSQ,        // Disqualified (Diskualifikasi)
    UNKNOWN     // Fallback jika API mengirim data aneh
}