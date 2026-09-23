package com.nexora.kaamsetu.feature.technician

import com.nexora.kaamsetu.core.session.SessionManager

/**
 * The current technician id. Phase 4B.01: now sourced from the active login
 * session instead of a hardcoded value. Falls back to the Phase 1-3 seeded
 * demo technician ("tech_1" / Ravi Kumar) only if somehow no session is
 * active, so nothing crashes mid-migration — in normal use post-login this
 * always resolves to the real logged-in technician's id.
 */
val DEMO_TECHNICIAN_ID: String
    get() = SessionManager.currentUserId ?: "tech_1"
    
