package com.hit.taptrack.data.sources.remote.responseBody

import kotlinx.serialization.Serializable

/**
 * Represents a standard error response returned by the remote API.
 *
 * @property error An optional message describing the nature of the error encountered during the request.
 */
@Serializable
data class ApiErrorResponse(
    val error: String? = null
)