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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexora.kaamsetu.core.navigation.TechnicianRoutes
import com.nexora.kaamsetu.core.navigation.TechnicianTab
import com.nexora.kaamsetu.domain.model.UserRole
import com.nexora.kaamsetu.feature.technician.active.TechnicianActiveJobsScreen
import com.nexora.kaamsetu.feature.technician.home.TechnicianHomeScreen
import com.nexora.kaamsetu.feature.technician.jobdetails.TechnicianJobDetailsScreen
import com.nexora.kaamsetu.feature.technician.jobs.TechnicianJobRequestsScreen
import com.nexora.kaamsetu.feature.technician.profile.TechnicianProfileScreen
import com.nexora.kaamsetu.feature.technician.sendquote.SendQuoteScreen

private val tabs = listOf(
    TechnicianTab.Home,
    TechnicianTab.JobRequests,
    TechnicianTab.ActiveJobs,
    TechnicianTab.Profile
)
private val tabRoutes = tabs.map { it.route }.toSet()

@Composable
fun TechnicianRootScreen(onExitRole: (UserRole?) -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            // Hide the bottom bar on full-screen flows (job details, send
            // quote) so they read as dedicated screens — same pattern as
            // CustomerRootScreen.
            if (currentRoute == null || currentRoute in tabRoutes) {
                NavigationBar {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TechnicianTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TechnicianTab.Home.route) { TechnicianHomeScreen() }

            composable(TechnicianTab.JobRequests.route) {
                TechnicianJobRequestsScreen(
                    onViewJob = { jobId -> navController.navigate(TechnicianRoutes.jobDetails(jobId)) }
                )
            }

            composable(TechnicianTab.ActiveJobs.route) { TechnicianActiveJobsScreen() }

            composable(TechnicianTab.Profile.route) {
                TechnicianProfileScreen(onSwitchRole = onExitRole)
            }

            composable(
                route = TechnicianRoutes.JOB_DETAILS,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId").orEmpty()
                TechnicianJobDetailsScreen(
                    jobId = jobId,
                    onSendOrUpdateQuote = { id -> navController.navigate(TechnicianRoutes.sendQuote(id)) }
                )
            }

            composable(
                route = TechnicianRoutes.SEND_QUOTE,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId").orEmpty()
                SendQuoteScreen(
                    jobId = jobId,
                    onQuoteSubmitted = {
                        // Back to Job Requests — the job will have dropped off
                        // this technician's own list once selected, or simply
                        // remain visible with an updated quote until then.
                        navController.navigate(TechnicianTab.JobRequests.route) {
                            popUpTo(TechnicianTab.JobRequests.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
