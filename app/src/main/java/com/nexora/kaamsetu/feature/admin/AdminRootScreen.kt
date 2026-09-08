package com.nexora.kaamsetu.feature.admin

import androidx.activity.compose.BackHandler
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
import com.nexora.kaamsetu.core.navigation.AdminTab
import com.nexora.kaamsetu.feature.admin.areas.AdminAreasScreen
import com.nexora.kaamsetu.feature.admin.dashboard.AdminDashboardScreen
import com.nexora.kaamsetu.feature.admin.jobs.AdminJobsScreen
import com.nexora.kaamsetu.feature.admin.services.AdminServicesScreen
import com.nexora.kaamsetu.feature.admin.technicians.AdminTechniciansScreen

private val tabs = listOf(
    AdminTab.Dashboard,
    AdminTab.Technicians,
    AdminTab.Jobs,
    AdminTab.Services,
    AdminTab.Areas
)

@Composable
fun AdminRootScreen(onExit: () -> Unit) {
    val navController = rememberNavController()
    BackHandler(onBack = onExit)

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
            startDestination = AdminTab.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AdminTab.Dashboard.route) { AdminDashboardScreen() }
            composable(AdminTab.Technicians.route) { AdminTechniciansScreen() }
            composable(AdminTab.Jobs.route) { AdminJobsScreen() }
            composable(AdminTab.Services.route) { AdminServicesScreen() }
            composable(AdminTab.Areas.route) { AdminAreasScreen() }
        }
    }
}

