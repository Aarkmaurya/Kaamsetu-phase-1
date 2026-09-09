package com.nexora.kaamsetu.feature.customer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexora.kaamsetu.core.navigation.CustomerRoutes
import com.nexora.kaamsetu.core.navigation.CustomerTab
import com.nexora.kaamsetu.domain.model.UserRole
import com.nexora.kaamsetu.feature.customer.createrequest.CreateServiceRequestScreen
import com.nexora.kaamsetu.feature.customer.home.CustomerHomeScreen
import com.nexora.kaamsetu.feature.customer.jobdetails.JobDetailsScreen
import com.nexora.kaamsetu.feature.customer.jobs.CustomerJobsScreen
import com.nexora.kaamsetu.feature.customer.profile.CustomerProfileScreen
import com.nexora.kaamsetu.feature.customer.services.CustomerServicesScreen

private val tabs = listOf(CustomerTab.Home, CustomerTab.Services, CustomerTab.MyJobs, CustomerTab.Profile)
private val tabRoutes = tabs.map { it.route }.toSet()

@Composable
fun CustomerRootScreen(onExitRole: (UserRole?) -> Unit) {
    val navController = rememberNavController()

    // Simple hoisted UI state shared between screens in this graph — avoids
    // threading a "just created a request" flag through nav arguments.
    var showJobCreatedBanner by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            // Hide the bottom bar on full-screen flows (create request, job
            // details) so they read as dedicated screens, not extra tab content.
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
            startDestination = CustomerTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(CustomerTab.Home.route) {
                CustomerHomeScreen(
                    onBrowseServices = {
                        navController.navigate(CustomerTab.Services.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onServiceSelected = { serviceId ->
                        navController.navigate(CustomerRoutes.createRequest(serviceId))
                    }
                )
            }

            composable(CustomerTab.Services.route) {
                CustomerServicesScreen(
                    onCategorySelected = { serviceId ->
                        navController.navigate(CustomerRoutes.createRequest(serviceId))
                    }
                )
            }

            composable(CustomerTab.MyJobs.route) {
                CustomerJobsScreen(
                    showSuccessBanner = showJobCreatedBanner,
                    onBannerDismissed = { showJobCreatedBanner = false },
                    onJobClick = { jobId -> navController.navigate(CustomerRoutes.jobDetails(jobId)) }
                )
            }

            composable(CustomerTab.Profile.route) {
                CustomerProfileScreen(onSwitchRole = onExitRole)
            }

            composable(
                route = CustomerRoutes.CREATE_REQUEST,
                arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId").orEmpty()
                CreateServiceRequestScreen(
                    serviceId = serviceId,
                    onRequestCreated = {
                        showJobCreatedBanner = true
                        navController.navigate(CustomerTab.MyJobs.route) {
                            popUpTo(CustomerTab.Home.route)
                            launchSingleTop = true
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = CustomerRoutes.JOB_DETAILS,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId").orEmpty()
                JobDetailsScreen(jobId = jobId)
            }
        }
    }
}

