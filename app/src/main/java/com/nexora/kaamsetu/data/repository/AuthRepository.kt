package com.nexora.kaamsetu.data.repository

import com.nexora.kaamsetu.domain.model.AuthUser
import com.nexora.kaamsetu.domain.model.UserRole

/**
 * BACKEND TODO (Phase 5): replace LocalAuthRepository with an implementation
 * that calls a real backend auth endpoint (login/register), receives a real
 * session/token, and lets the backend be the actual authority on identity and
 * role — this interface's shape should not need to change for that swap.
 * Nothing client-side here is a real authorization boundary; see
 * PasswordHasher and SessionManager for what specifically is not
 * production-grade yet.
 */
interface AuthRepository {
    suspend fun login(phone: String, password: String, expectedRole: UserRole): Result<AuthUser>
    suspend fun register(name: String, phone: String, password: String, role: UserRole): Result<AuthUser>
    fun logout()
    suspend fun getCurrentUser(): AuthUser?
}
