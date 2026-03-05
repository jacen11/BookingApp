package dev.pastukhov.booking.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.pastukhov.booking.presentation.ui.screens.mybookings.MyBookingsScreen

/**
 * Navigation configuration for My Bookings screen.
 */
object MyBookingsScreenNavigation {


    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(route = Screen.MyBookings.route) {
            MyBookingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookNow = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.MyBookings.route)
                    }
                },
                onBookingClick = { bookingId ->
                    navController.navigate(Screen.BookingDetail.createRoute(bookingId))
                },
                onRepeatBooking = { booking ->
                    navController.navigate(
                        Screen.SelectDateTime.createRoute(
                            providerId = booking.providerId,
                            serviceId = booking.serviceId
                        )
                    )
                },
                onRateService = { booking ->
                    // TODO: Implement rating navigation
                }
            )
        }
    }
}
