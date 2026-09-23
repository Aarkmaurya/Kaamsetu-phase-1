package com.nexora.kaamsetu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexora.kaamsetu.core.di.AppContainer
import com.nexora.kaamsetu.core.di.LocalAppContainer
import com.nexora.kaamsetu.core.navigation.AuthRoutes
import com.nexora.kaamsetu.core.navigation.TopLevelRoute
import com.nexora.kaamsetu.core.session.SessionManager
import com.nexora.kaamsetu.core.theme.KaamSetuTheme
import com.nexora.kaamsetu.domain.model.UserRole
import com.nexora.kaamsetu.feature.auth.login.LoginScreen
import com.nexora.kaamsetu.feature.auth.register.RegisterScreen
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

@Composable
private fun KaamSetuApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = TopLevelRoute.SPLASH) {
        composable(TopLevelRoute.SPLASH) {
            SplashScreen(
                onFinished = {
                    // Phase 4B.01: check for an existing local session once,
                    // right after splash. An already-logged-in user skips
                    // Role Select and Login entirely. This is a convenience
                    // check against local SharedPreferences, not a real
                    // session validation — see SessionManager's header for
                    // what Phase 5 must replace this with.
                    val destination = when (SessionManager.currentRole) {
                        UserRole.CUSTOMER -> TopLevelRoute.CUSTOMER_ROOT
                        UserRole.TECHNICIAN -> TopLevelRoute.TECHNICIAN_ROOT
                        null -> TopLevelRoute.ROLE_SELECT
                    }
                    navController.navigate(destination) {
                        popUpTo(TopLevelRoute.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(TopLevelRoute.ROLE_SELECT) {
            RoleSelectionScreen(
                onRoleSelected = { role -> navController.navigate(AuthRoutes.login(role)) }
            )
        }

        composable(
            route = AuthRoutes.LOGIN,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = parseRoleArg(backStackEntry.arguments?.getString("role"))
            LoginScreen(
                role = role,
                onLoginSuccess = { loggedInRole -> enterRoleRoot(navController, loggedInRole) },
                onNavigateToRegister = { navController.navigate(AuthRoutes.register(role)) }
            )
        }

        composable(
            route = AuthRoutes.REGISTER,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = parseRoleArg(backStackEntry.arguments?.getString("role"))
            RegisterScreen(
                role = role,
                onRegisterSuccess = { registeredRole -> enterRoleRoot(navController, registeredRole) }
            )
        }

        composable(TopLevelRoute.CUSTOMER_ROOT) {
            CustomerRootScreen(
                onLogout = { returnToRoleSelectAfterLogout(navController) }
            )
        }

        composable(TopLevelRoute.TECHNICIAN_ROOT) {
            TechnicianRootScreen(
                onLogout = { returnToRoleSelectAfterLogout(navController) }
            )
        }
    }
}

/** Role Selection only ever navigates here with "customer" or "technician" — never "admin", which no longer exists as a value. */
private fun parseRoleArg(value: String?): UserRole =
    value?.let { runCatching { UserRole.valueOf(it) }.getOrNull() } ?: UserRole.CUSTOMER

private fun enterRoleRoot(navController: NavHostController, role: UserRole) {
    val destination = when (role) {
        UserRole.CUSTOMER -> TopLevelRoute.CUSTOMER_ROOT
        UserRole.TECHNICIAN -> TopLevelRoute.TECHNICIAN_ROOT
    }
    // Clear Splash/RoleSelect/Login/Register from the back stack so system
    // back from the role root exits the app, not back into a logged-out screen.
    navController.navigate(destination) {
        popUpTo(TopLevelRoute.SPLASH) { inclusive = true }
    }
}

/**
 * The session was already cleared by the profile screen's logout action
 * (see CustomerProfileViewModel/TechnicianProfileViewModel) before this is
 * called — this just resets navigation back to Role Select with an empty
 * back stack, so back-button can't return into the now-logged-out role root.
 */
private fun returnToRoleSelectAfterLogout(navController: NavHostController) {
    navController.navigate(TopLevelRoute.ROLE_SELECT) {
        popUpTo(0) { inclusive = true }
    }
}

