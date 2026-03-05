package dev.pastukhov.booking.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dev.pastukhov.booking.presentation.ui.screens.bookingdetails.BookingDetailsScreen

/**
 * Navigation configuration for Booking Detail screen.
 */
object BookingDetailScreenNavigation {


    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(
            route = Screen.BookingDetail.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "myapp://booking_details/{bookingId}"
                },
                navDeepLink {
                    uriPattern = "https://bookingapp.com/booking/{bookingId}"
                }
            )
        ) { backStackEntry ->
            val bookingId =
                backStackEntry.arguments?.getString("bookingId") ?: return@composable
            BookingDetailsScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProvider = { providerId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(providerId))
                },
                onNavigateToRepeatBooking = { providerId, serviceId ->
                    navController.navigate(
                        Screen.SelectDateTime.createRoute(providerId, serviceId)
                    )
                },
                onNavigateToRate = { id ->
                    // TODO: Navigate to rating screen
                }
            )
        }
    }
}
