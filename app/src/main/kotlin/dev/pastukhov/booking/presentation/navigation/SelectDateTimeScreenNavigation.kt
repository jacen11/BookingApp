package dev.pastukhov.booking.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pastukhov.booking.presentation.ui.screens.booking.SelectDateTimeScreen

/**
 * Navigation configuration for Select Date/Time screen.
 */
object SelectDateTimeScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(
            route = Screen.SelectDateTime.route,
            arguments = listOf(
                navArgument("providerId") { type = NavType.StringType },
                navArgument("serviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val providerId =
                backStackEntry.arguments?.getString("providerId") ?: return@composable
            val serviceId =
                backStackEntry.arguments?.getString("serviceId") ?: return@composable

            SelectDateTimeScreen(
                providerId = providerId,
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onNext = {
                    navController.navigate(
                        Screen.ConfirmBooking.createRoute(
                            providerId = providerId,
                            serviceId = serviceId,
                            date = "",
                            time = ""
                        )
                    )
                }
            )
        }
    }
}
