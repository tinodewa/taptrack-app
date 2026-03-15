package com.hit.taptrack.data.model

data class TicketModel(
    val registrationId: String,
    val eventName: String,
    val date: String,
    val location: String,
    val category: String,
    val racerNumber: String, // Nomor lambung/start pembalap
    val qrCodePayload: String, // Data mentah untuk di-generate jadi QR (misal: "REG-12345")
    val status: String // misal: "REGISTERED", "VERIFIED", "FINISHED"
)