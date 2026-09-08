package com.nexora.kaamsetu.feature.technician

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nexora.kaamsetu.core.navigation.TechnicianTab
import com.nexora.kaamsetu.domain.model.UserRole
import com.nexora.kaamsetu.feature.technician.active.TechnicianActiveJobsScreen
import com.nexora.kaamsetu.feature.technician.home.TechnicianHomeScreen
import com.nexora.kaamsetu.feature.technician.jobs.TechnicianJobRequestsScreen
import com.nexora.kaamsetu.feature.technician.profile.TechnicianProfileScreen

private val tabs = listOf(
    TechnicianTab.Home,
    TechnicianTab.JobRequests,
    TechnicianTab.ActiveJobs,
    TechnicianTab.Profile
)

@Composable
fun TechnicianRootScreen(onExitRole: (UserRole?) -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                tabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TechnicianTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TechnicianTab.Home.route) { TechnicianHomeScreen() }
            composable(TechnicianTab.JobRequests.route) { TechnicianJobRequestsScreen() }
            composable(TechnicianTab.ActiveJobs.route) { TechnicianActiveJobsScreen() }
            composable(TechnicianTab.Profile.route) { TechnicianProfileScreen(onSwitchRole = onExitRole) }
        }
    }
}
