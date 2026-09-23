package com.nexora.kaamsetu.data.repository

import com.nexora.kaamsetu.core.security.PasswordHasher
import com.nexora.kaamsetu.core.session.SessionManager
import com.nexora.kaamsetu.data.local.CustomerDao
import com.nexora.kaamsetu.data.local.CustomerEntity
import com.nexora.kaamsetu.data.local.TechnicianDao
import com.nexora.kaamsetu.data.local.TechnicianEntity
import com.nexora.kaamsetu.data.local.UserAccountDao
import com.nexora.kaamsetu.data.local.UserAccountEntity
import com.nexora.kaamsetu.domain.model.AuthUser
import com.nexora.kaamsetu.domain.model.UserRole
import java.util.UUID

/**
 * Local/demo auth backed by Room. See AuthRepository's header comment for
 * what Phase 5 must replace here, and PasswordHasher's header for exactly
 * what is (and is not) protected about the stored password.
 */
class LocalAuthRepository(
    private val userAccountDao: UserAccountDao,
    private val customerDao: CustomerDao,
    private val technicianDao: TechnicianDao
) : AuthRepository {

    override suspend fun login(phone: String, password: String, expectedRole: UserRole): Result<AuthUser> {
        val account = userAccountDao.findByPhone(phone.trim())
            ?: return Result.failure(IllegalArgumentException("No account found for this phone number."))

        if (!PasswordHasher.matches(password, account.passwordHash)) {
            return Result.failure(IllegalArgumentException("Incorrect password."))
        }

        val actualRole = runCatching { UserRole.valueOf(account.role) }.getOrNull()
        if (actualRole != expectedRole) {
            val roleLabel = actualRole?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "a different role"
            return Result.failure(IllegalArgumentException("This account is registered as $roleLabel, not ${expectedRole.name.lowercase()}."))
        }

        SessionManager.save(account.id, actualRole)
        return Result.success(AuthUser(id = account.id, name = account.name, phone = account.phone, role = actualRole))
    }

    override suspend fun register(name: String, phone: String, password: String, role: UserRole): Result<AuthUser> {
        val trimmedPhone = phone.trim()
        if (userAccountDao.findByPhone(trimmedPhone) != null) {
            return Result.failure(IllegalArgumentException("An account already exists for this phone number."))
        }

        val id = "user_${UUID.randomUUID()}"
        val account = UserAccountEntity(
            id = id,
            phone = trimmedPhone,
            passwordHash = PasswordHasher.hash(password),
            name = name.trim(),
            role = role.name,
            createdAt = System.currentTimeMillis()
        )

        val inserted = runCatching { userAccountDao.insert(account) }
        if (inserted.isFailure) {
            return Result.failure(IllegalArgumentException("An account already exists for this phone number."))
        }

        // Create the matching domain-level row so existing Phase 1-3
        // repositories (which key by this same id) work unchanged for a real
        // newly-registered user, not just the seeded demo identities.
        when (role) {
            UserRole.CUSTOMER -> customerDao.insertAll(
                listOf(CustomerEntity(id = id, name = account.name, phone = trimmedPhone))
            )
            UserRole.TECHNICIAN -> technicianDao.insertAll(
                listOf(
                    TechnicianEntity(
                        id = id,
                        name = account.name,
                        phone = trimmedPhone,
                        // New technicians start PENDING — approval now happens
                        // through the separate web Admin dashboard, not Android.
                        status = "PENDING",
                        skillsCsv = "",
                        city = "",
                        rating = 0.0,
                        completedJobs = 0,
                        isVerified = false,
                        isAvailable = false
                    )
                )
            )
        }

        SessionManager.save(id, role)
        return Result.success(AuthUser(id = id, name = account.name, phone = trimmedPhone, role = role))
    }

    override fun logout() {
        SessionManager.clear()
    }

    override suspend fun getCurrentUser(): AuthUser? {
        val userId = SessionManager.currentUserId ?: return null
        val role = SessionManager.currentRole ?: return null
        val account = userAccountDao.findById(userId) ?: return null
        return AuthUser(id = account.id, name = account.name, phone = account.phone, role = role)
    }
}
