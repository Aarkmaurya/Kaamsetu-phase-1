package com.nexora.kaamsetu.core.security

import java.security.MessageDigest

/**
 * SHA-256 password hashing for the local demo account store.
 *
 * THIS IS NOT PRODUCTION-GRADE AUTHENTICATION. It exists only so passwords
 * are not stored in plain text in the local Room database during the MVP.
 * A real backend (Phase 5) must use a proper adaptive password hash
 * (bcrypt/scrypt/Argon2) with a unique per-user salt, performed server-side —
 * never trust a client to hash its own password correctly, and never ship
 * this exact scheme to production.
 */
object PasswordHasher {

    // A single static pepper is a small speed bump, not real protection —
    // there is no per-user salt here. Do not reuse this approach server-side.
    private const val STATIC_PEPPER = "kaamsetu_local_demo_pepper_v1"

    fun hash(rawPassword: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((rawPassword + STATIC_PEPPER).toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    fun matches(rawPassword: String, hashedPassword: String): Boolean {
        return hash(rawPassword) == hashedPassword
    }
}
