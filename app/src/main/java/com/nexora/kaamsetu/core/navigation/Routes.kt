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

sealed class TechnicianTab(val route: String, val label: String, val icon: ImageVector) {
    object Home : TechnicianTab("technician/home", "Home", Icons.Filled.Home)
    object JobRequests : TechnicianTab("technician/job_requests", "Job Requests", Icons.Filled.List)
    object ActiveJobs : TechnicianTab("technician/active_jobs", "Active Jobs", Icons.Filled.Build)
    object Profile : TechnicianTab("technician/profile", "Profile", Icons.Filled.Person)
}

sealed class AdminTab(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : AdminTab("admin/dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Technicians : AdminTab("admin/technicians", "Technicians", Icons.Filled.CheckCircle)
    object Jobs : AdminTab("admin/jobs", "Jobs", Icons.Filled.Work)
    object Services : AdminTab("admin/services", "Services", Icons.Filled.Place)
    object Areas : AdminTab("admin/areas", "Areas", Icons.Filled.LocationOn)
}

