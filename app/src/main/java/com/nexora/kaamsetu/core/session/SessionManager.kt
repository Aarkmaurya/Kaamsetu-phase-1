package com.nexora.kaamsetu.core.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.nexora.kaamsetu.domain.model.UserRole

/**
 * Persists only a user id and role across app restarts — never a password or
 * any real token, because there isn't one yet (see PasswordHasher and
 * LocalAuthRepository). Deliberately built on plain Android SharedPreferences
 * rather than adding a DataStore dependency, consistent with this project's
 * "no unnecessary dependencies" approach.
 *
 * BACKEND TODO (Phase 5): replace this with whatever real session mechanism
 * the backend issues (e.g. a securely stored auth token with expiry), behind
 * this same currentUserId/currentRole/save/clear surface so callers do not
 * need to change.
 */
object SessionManager {
    private const val PREFS_NAME = "kaamsetu_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_ROLE = "role"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    val currentUserId: String?
        get() = prefs?.getString(KEY_USER_ID, null)

    val currentRole: UserRole?
        get() = prefs?.getString(KEY_ROLE, null)?.let { stored ->
            runCatching { UserRole.valueOf(stored) }.getOrNull()
        }

    fun save(userId: String, role: UserRole) {
        prefs?.edit { putString(KEY_USER_ID, userId); putString(KEY_ROLE, role.name) }
    }

    fun clear() {
        prefs?.edit { clear() }
    }
}

