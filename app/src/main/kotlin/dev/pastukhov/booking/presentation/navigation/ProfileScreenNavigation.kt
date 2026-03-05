package dev.pastukhov.booking.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.pastukhov.booking.presentation.ui.screens.profile.ProfileScreen

/**
 * Navigation configuration for Profile screen.
 */
object ProfileScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToBookings = {
                    navController.navigate(Screen.MyBookings.route)
                }
            )
        }
    }
}
