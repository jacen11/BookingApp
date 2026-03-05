package dev.pastukhov.booking.presentation.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pastukhov.booking.presentation.model.BookingSuccessEvent
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingSuccessScreen
import dev.pastukhov.booking.presentation.viewmodel.BookingSuccessViewModel
import java.time.LocalDate
import java.time.LocalTime

/**
 * Navigation configuration for Booking Success screen.
 */
object BookingSuccessScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(
            route = Screen.BookingSuccess.route,
            arguments = listOf(
                navArgument("providerId") { type = NavType.StringType },
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val providerId =
                backStackEntry.arguments?.getString("providerId") ?: return@composable
            val serviceId =
                backStackEntry.arguments?.getString("serviceId") ?: return@composable
            val date = backStackEntry.arguments?.getString("date") ?: return@composable
            val time = backStackEntry.arguments?.getString("time") ?: return@composable
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: return@composable

            val viewModel: BookingSuccessViewModel = hiltViewModel()
            viewModel.handleEvent(
                BookingSuccessEvent.InitializeBooking(
                    providerId = providerId,
                    serviceId = serviceId,
                    date = LocalDate.parse(date),
                    time = LocalTime.parse(time),
                    bookingId = bookingId
                )
            )

            BookingSuccessScreen(
                onViewBookings = {
                    navController.navigate(Screen.MyBookings.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onViewDetails = { bookingId ->
                    navController.navigate(Screen.BookingDetail.createRoute(bookingId))
                },
                onDone = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
