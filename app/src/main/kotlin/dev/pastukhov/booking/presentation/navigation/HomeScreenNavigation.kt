package dev.pastukhov.booking.presentation.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.pastukhov.booking.presentation.ui.screens.home.HomeScreen
import dev.pastukhov.booking.presentation.viewmodel.HomeViewModel

/**
 * Navigation configuration for Home screen.
 */
object HomeScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(route = Screen.Home.route) { backStackEntry ->
            val viewModel: HomeViewModel = hiltViewModel(backStackEntry)

            HomeScreen(
                viewModel = viewModel,
                onProviderClick = { providerId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(providerId))
                }
            )
        }
    }
}
