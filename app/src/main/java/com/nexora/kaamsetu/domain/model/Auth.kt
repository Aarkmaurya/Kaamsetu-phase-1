package com.nexora.kaamsetu.domain.model

/**
 * The authenticated identity for the current session. `id` doubles as the
 * Customer/Technician row id in Room (see LocalAuthRepository.register) so
 * existing Phase 1-3 repositories keep working unchanged once a real user is
 * logged in instead of a hardcoded demo id.
 */
data class AuthUser(
    val id: String,
    val name: String,
    val phone: String,
    val role: UserRole
)

/**
 * UI-facing auth state. Kept deliberately small and screen-agnostic — Login
 * and Register each hold their own form-field state locally and only reach
 * into this for the overall outcome of the in-flight attempt.
 */
sealed interface AuthState {
    data object LoggedOut : AuthState
    data object Loading : AuthState
    data class LoggedIn(val user: AuthUser) : AuthState
    data class Error(val message: String) : AuthState
}
