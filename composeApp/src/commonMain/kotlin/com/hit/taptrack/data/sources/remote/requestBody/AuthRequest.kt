package com.hit.taptrack.data.sources.remote.requestBody

import kotlinx.serialization.Serializable

// --- MODEL DATA (DTO) ---
@Serializable
data class AuthRequest(val email: String, val password: String)