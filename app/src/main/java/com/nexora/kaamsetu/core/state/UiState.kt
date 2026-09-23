package com.nexora.kaamsetu.core.state

/**
 * Standard shape for a screen's data-loading state. New screens (and Phase 5
 * API-backed screens) should model their state as one of these instead of
 * inventing ad hoc loading/error booleans per screen.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
}
