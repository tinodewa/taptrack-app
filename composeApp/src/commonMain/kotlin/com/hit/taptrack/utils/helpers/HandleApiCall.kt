package com.hit.taptrack.utils.helpers

import com.hit.taptrack.common.state.ResultState
import com.hit.taptrack.data.sources.remote.responseBody.ApiErrorResponse
import de.jensklingenberg.ktorfit.Response
import io.github.aakira.napier.Napier
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

/**
 * A wrapper function to execute a network request and handle its response or potential exceptions.
 *
 * This function handles:
 * - Successful responses by returning [ResultState.Success] with the response body.
 * - API errors by attempting to parse [ApiErrorResponse] from the error body.
 * - Common network exceptions such as [SocketTimeoutException] and [IOException].
 * - Generic exceptions, providing a fallback error state.
 *
 * @param T The type of the expected response body.
 * @param apiCall A suspending lambda that performs the network request and returns a [Response].
 * @param tag A string used for logging purposes to identify the source of the API call.
 * @return A [ResultState] representing either a [ResultState.Success] containing the data
 * or a [ResultState.Error] containing the status code and error message.
 */
suspend fun <T : Any> handleApiCall(
    apiCall: suspend () -> Response<T>,
    tag: String
): ResultState<T> {
    return try {
        val response = apiCall()
        val body = response.body()

        if (response.isSuccessful) {
            if (body != null) {
                // Case 1: Standard Success with Data (200 OK)
                ResultState.Success(body)
            } else if (response.code == 204 || response.code == 205) {
                // Case 2: Success with No Content (204/205)
                // We cast 'Unit' to T. This works only if your API expects Response<Unit>
                @Suppress("UNCHECKED_CAST")
                ResultState.Success(Unit as T)
            } else {
                // Case 3: Success code (e.g. 200) but body was unexpectedly null
                ResultState.Error(
                    statusCode = response.code,
                    message = "Response body is null"
                )
            }
        } else {
            // 1. Try to read the raw error body
            // Ktorfit errorBody() returns the raw content if the request failed
            val errorBodyString = response.errorBody() as? String

            // 2. Parse the JSON manually
            val customErrorMessage = try {
                if (!errorBodyString.isNullOrEmpty()) {
                    val json = Json { ignoreUnknownKeys = true }
                    val errorObj = json.decodeFromString<ApiErrorResponse>(errorBodyString)
                    errorObj.error
                } else {
                    null
                }
            } catch (e: Exception) {
                Napier.e("Failed to parse error JSON", e, tag)
                null
            }

            // 3. Fallback logic: Parsed Error -> Raw Message -> Status Code
            val finalMessage = customErrorMessage
                ?: response.message

            ResultState.Error(
                statusCode = response.code,
                message = finalMessage
            )
        }
    } catch (e: SocketTimeoutException) {
        Napier.e(tag = tag, message = "Request Timed Out: ${e.cause?.message}")
        ResultState.Error(
            statusCode = 408,
            message = "Request timed out. Please try again."
        )
    } catch (e: IOException) {
        Napier.e(tag = tag, message = "No internet connection: ${e.cause?.message}")
        ResultState.Error(
            statusCode = 503,
            message = "No internet connection. Please check your network and try again."
        )
    } catch (e: Exception) {
        Napier.e(tag = tag, message = "${e.cause?.message} :: ${e.stackTraceToString()}")
        ResultState.Error(
            statusCode = 500,
            message = "Unhandled Error: ${e.cause?.message ?: "An unknown error occurred"}",
        )
    }
}