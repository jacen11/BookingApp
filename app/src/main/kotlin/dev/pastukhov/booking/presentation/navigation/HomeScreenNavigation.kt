package dev.pastukhov.booking.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.pastukhov.booking.presentation.ui.screens.home.HomeScreen

/**
 * Navigation configuration for Home screen.
 */
object HomeScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onProviderClick = { providerId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(providerId))
                }
            )
        }
    }
}
