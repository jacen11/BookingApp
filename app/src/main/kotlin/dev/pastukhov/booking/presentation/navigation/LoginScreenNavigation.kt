package dev.pastukhov.booking.presentation.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.pastukhov.booking.presentation.ui.screens.login.LoginScreen
import dev.pastukhov.booking.presentation.viewmodel.LoginViewModel

/**
 * Navigation configuration for Login screen.
 */
object LoginScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(route = Screen.Login.route) { backStackEntry ->
            // Create ViewModel in Navigation (outside of Screen Composable)
            val viewModel: LoginViewModel = hiltViewModel(backStackEntry)

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
