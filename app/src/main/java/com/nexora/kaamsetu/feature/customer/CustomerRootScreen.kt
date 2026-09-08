package com.nexora.kaamsetu.feature.customer

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
import com.nexora.kaamsetu.core.navigation.CustomerTab
import com.nexora.kaamsetu.domain.model.UserRole
import com.nexora.kaamsetu.feature.customer.home.CustomerHomeScreen
import com.nexora.kaamsetu.feature.customer.jobs.CustomerJobsScreen
import com.nexora.kaamsetu.feature.customer.profile.CustomerProfileScreen
import com.nexora.kaamsetu.feature.customer.services.CustomerServicesScreen

private val tabs = listOf(CustomerTab.Home, CustomerTab.Services, CustomerTab.MyJobs, CustomerTab.Profile)

@Composable
fun CustomerRootScreen(onExitRole: (UserRole?) -> Unit) {
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
            startDestination = CustomerTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(CustomerTab.Home.route) {
                CustomerHomeScreen(onBrowseServices = { navController.navigate(CustomerTab.Services.route) })
            }
            composable(CustomerTab.Services.route) {
                CustomerServicesScreen(onCategorySelected = { /* future: open request-creation flow */ })
            }
            composable(CustomerTab.MyJobs.route) {
                CustomerJobsScreen()
            }
            composable(CustomerTab.Profile.route) {
                CustomerProfileScreen(onSwitchRole = onExitRole)
            }
        }
    }
}

