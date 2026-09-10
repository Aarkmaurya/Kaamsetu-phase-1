package com.nexora.kaamsetu.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

/** Top-level app destinations, before a role's own bottom-nav graph takes over. */
object TopLevelRoute {
    const val SPLASH = "splash"
    const val ROLE_SELECT = "role_select"
    const val CUSTOMER_ROOT = "customer_root"
    const val TECHNICIAN_ROOT = "technician_root"
    const val ADMIN_ROOT = "admin_root"
}

sealed class CustomerTab(val route: String, val label: String, val icon: ImageVector) {
    object Home : CustomerTab("customer/home", "Home", Icons.Filled.Home)
    object Services : CustomerTab("customer/services", "Services", Icons.Filled.List)
    object MyJobs : CustomerTab("customer/my_jobs", "My Jobs", Icons.Filled.Work)
    object Profile : CustomerTab("customer/profile", "Profile", Icons.Filled.Person)
}

/**
 * Phase 2 destinations layered on top of the Phase 1 bottom-nav tabs above.
 * These live inside the same customer NavHost (see CustomerRootScreen) but are
 * not tabs themselves — the bottom bar is hidden while on them.
 */
object CustomerRoutes {
    private const val CREATE_REQUEST_BASE = "customer/create_request"
    const val CREATE_REQUEST = "$CREATE_REQUEST_BASE/{serviceId}"
    fun createRequest(serviceId: String) = "$CREATE_REQUEST_BASE/$serviceId"

    private const val JOB_DETAILS_BASE = "customer/job_details"
    const val JOB_DETAILS = "$JOB_DETAILS_BASE/{jobId}"
    fun jobDetails(jobId: String) = "$JOB_DETAILS_BASE/$jobId"

    /** Phase 3: compare-quotes screen, reached from Job Details once quotes exist. */
    private const val QUOTES_BASE = "customer/quotes"
    const val QUOTES = "$QUOTES_BASE/{jobId}"
    fun quotes(jobId: String) = "$QUOTES_BASE/$jobId"
}

sealed class TechnicianTab(val route: String, val label: String, val icon: ImageVector) {
    object Home : TechnicianTab("technician/home", "Home", Icons.Filled.Home)
    object JobRequests : TechnicianTab("technician/job_requests", "Job Requests", Icons.Filled.List)
    object ActiveJobs : TechnicianTab("technician/active_jobs", "Active Jobs", Icons.Filled.Build)
    object Profile : TechnicianTab("technician/profile", "Profile", Icons.Filled.Person)
}

/**
 * Phase 3 destinations layered on top of the technician bottom-nav tabs above —
 * same pattern as CustomerRoutes: live in the technician NavHost, not tabs,
 * bottom bar hidden while on them (see TechnicianRootScreen).
 */
object TechnicianRoutes {
    private const val JOB_DETAILS_BASE = "technician/job_details"
    const val JOB_DETAILS = "$JOB_DETAILS_BASE/{jobId}"
    fun jobDetails(jobId: String) = "$JOB_DETAILS_BASE/$jobId"

    private const val SEND_QUOTE_BASE = "technician/send_quote"
    const val SEND_QUOTE = "$SEND_QUOTE_BASE/{jobId}"
    fun sendQuote(jobId: String) = "$SEND_QUOTE_BASE/$jobId"
}

sealed class AdminTab(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : AdminTab("admin/dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Technicians : AdminTab("admin/technicians", "Technicians", Icons.Filled.CheckCircle)
    object Jobs : AdminTab("admin/jobs", "Jobs", Icons.Filled.Work)
    object Services : AdminTab("admin/services", "Services", Icons.Filled.Place)
    object Areas : AdminTab("admin/areas", "Areas", Icons.Filled.LocationOn)
}
