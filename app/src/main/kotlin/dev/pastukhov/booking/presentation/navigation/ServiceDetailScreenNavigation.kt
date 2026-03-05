package dev.pastukhov.booking.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pastukhov.booking.presentation.ui.screens.ProviderDetailScreen

/**
 * Navigation configuration for Service/Provider Detail screen.
 */
object ServiceDetailScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(
            route = Screen.ServiceDetail.route,
            arguments = listOf(
                navArgument("providerId") { type = NavType.StringType }
            )
        ) { _ ->
            ProviderDetailScreen(
                onBookingClick = { providerId, serviceId ->
                    navController.navigate(
                        Screen.SelectDateTime.createRoute(
                            providerId,
                            serviceId
                        )
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
