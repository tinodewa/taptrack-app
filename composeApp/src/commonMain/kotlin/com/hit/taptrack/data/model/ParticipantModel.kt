package com.hit.taptrack.data.model

import com.hit.taptrack.common.state.ParticipantStatus

data class ParticipantModel(
    val registrationId: String,
    val racerName: String,
    val racerNumber: String,
    val category: String,
    val status: ParticipantStatus
)