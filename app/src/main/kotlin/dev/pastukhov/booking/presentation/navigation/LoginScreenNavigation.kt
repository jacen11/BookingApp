package dev.pastukhov.booking.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.presentation.ui.screens.login.LoginScreen

/**
 * Navigation configuration for Login screen.
 */
object LoginScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController,
        userSettingsRepository: UserSettingsRepository
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                userSettingsRepository = userSettingsRepository,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
