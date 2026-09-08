package com.nexora.kaamsetu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexora.kaamsetu.core.di.AppContainer
import com.nexora.kaamsetu.core.di.LocalAppContainer
import com.nexora.kaamsetu.core.navigation.TopLevelRoute
import com.nexora.kaamsetu.core.theme.KaamSetuTheme
import com.nexora.kaamsetu.domain.model.UserRole
import com.nexora.kaamsetu.feature.admin.AdminRootScreen
import com.nexora.kaamsetu.feature.customer.CustomerRootScreen
import com.nexora.kaamsetu.feature.roleselect.RoleSelectionScreen
import com.nexora.kaamsetu.feature.splash.SplashScreen
import com.nexora.kaamsetu.feature.technician.TechnicianRootScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appContainer = remember { AppContainer(applicationContext) }

            CompositionLocalProvider(LocalAppContainer provides appContainer) {
                KaamSetuTheme {
                    KaamSetuApp()
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun KaamSetuApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = TopLevelRoute.SPLASH) {
        composable(TopLevelRoute.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(TopLevelRoute.ROLE_SELECT) {
                        popUpTo(TopLevelRoute.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(TopLevelRoute.ROLE_SELECT) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    val destination = when (role) {
                        UserRole.CUSTOMER -> TopLevelRoute.CUSTOMER_ROOT
                        UserRole.TECHNICIAN -> TopLevelRoute.TECHNICIAN_ROOT
                        UserRole.ADMIN -> TopLevelRoute.ADMIN_ROOT
                    }
                    navController.navigate(destination) {
                        popUpTo(TopLevelRoute.ROLE_SELECT) { inclusive = true }
                    }
                }
            )
        }

        composable(TopLevelRoute.CUSTOMER_ROOT) {
            CustomerRootScreen(
                onExitRole = { returnToRoleSelect(navController) }
            )
        }

        composable(TopLevelRoute.TECHNICIAN_ROOT) {
            TechnicianRootScreen(
                onExitRole = { returnToRoleSelect(navController) }
            )
        }

        composable(TopLevelRoute.ADMIN_ROOT) {
            AdminRootScreen(
                onExit = { returnToRoleSelect(navController) }
            )
        }
    }
}

private fun returnToRoleSelect(navController: androidx.navigation.NavHostController) {
    navController.navigate(TopLevelRoute.ROLE_SELECT) {
        popUpTo(0) { inclusive = true }
    }
}

