package com.nexora.kaamsetu.core.network

/**
 * Error categories a future real API layer will actually produce. Nothing in
 * Phase 4B.01 makes real network calls yet — this exists so Phase 5 can wrap
 * real exceptions (IOException, SocketTimeoutException, HTTP 5xx, etc.) into
 * one of these without any UI code changing. Screens should always render
 * [userMessage], never a raw exception's message or stack trace.
 *
 * IMPORTANT: do not use this for Room/local-only failures on screens that
 * have no network dependency at all — a local database read failing is not
 * "no internet". See JobRepository/TechnicianRepository etc., which remain
 * plain suspend/Flow functions with no network framing.
 */
sealed class AppError(val userMessage: String) {
    data object NoInternet : AppError(
        "Internet connection nahi hai. Please check your connection and try again."
    )

    data object Timeout : AppError(
        "The request took too long. Please try again."
    )

    data object ServerError : AppError(
        "Something went wrong on our end. Please try again in a moment."
    )

    data class Unknown(val technicalDetail: String? = null) : AppError(
        "Something went wrong. Please try again."
    )
}

