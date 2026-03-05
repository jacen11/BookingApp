package dev.pastukhov.booking.presentation.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingConfirmationScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingConfirmationViewModel
import java.time.LocalDate
import java.time.LocalTime

/**
 * Navigation configuration for Confirm Booking screen.
 */
object ConfirmBookingScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(
            route = Screen.ConfirmBooking.route,
            arguments = listOf(
                navArgument("providerId") { type = NavType.StringType },
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val providerId =
                backStackEntry.arguments?.getString("providerId") ?: return@composable
            val serviceId =
                backStackEntry.arguments?.getString("serviceId") ?: return@composable
            val date = backStackEntry.arguments?.getString("date") ?: return@composable
            val time = backStackEntry.arguments?.getString("time") ?: return@composable

            val viewModel: BookingConfirmationViewModel = hiltViewModel()
            viewModel.initializeBooking(
                providerId = providerId,
                serviceId = serviceId,
                date = LocalDate.parse(date),
                time = LocalTime.parse(time)
            )

            BookingConfirmationScreen(
                providerId = providerId,
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onNext = {
                    navController.navigate(
                        Screen.Payment.createRoute(
                            providerId = providerId,
                            serviceId = serviceId,
                            date = date,
                            time = time
                        )
                    )
                },
                viewModel = viewModel
            )
        }
    }
}
