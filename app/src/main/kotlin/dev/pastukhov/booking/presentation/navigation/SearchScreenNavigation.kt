package dev.pastukhov.booking.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.pastukhov.booking.presentation.ui.screens.search.SearchScreen

/**
 * Navigation configuration for Search screen.
 */
object SearchScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(route = "search") {
            SearchScreen(
                onProviderClick = { providerId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(providerId))
                }
            )
        }
    }
}
