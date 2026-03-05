package dev.pastukhov.booking.presentation.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pastukhov.booking.presentation.model.PaymentEvent
import dev.pastukhov.booking.presentation.ui.screens.booking.PaymentScreen
import dev.pastukhov.booking.presentation.viewmodel.PaymentViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Navigation configuration for Payment screen.
 */
object PaymentScreenNavigation {

    fun NavGraphBuilder.createNavGraph(
        navController: NavHostController
    ) {
        composable(
            route = Screen.Payment.route,
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
            val dateString = backStackEntry.arguments?.getString("date") ?: ""
            val timeString = backStackEntry.arguments?.getString("time") ?: ""

            // Validate that date and time are not empty
            if (dateString.isBlank() || timeString.isBlank()) {
                navController.popBackStack()
                return@composable
            }

            val date = try {
                LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: Exception) {
                navController.popBackStack()
                return@composable
            }

            val time = try {
                LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: Exception) {
                navController.popBackStack()
                return@composable
            }

            val viewModel: PaymentViewModel = hiltViewModel()
            viewModel.handleEvent(
                PaymentEvent.InitializePayment(
                    providerId = providerId,
                    serviceId = serviceId,
                    date = date,
                    time = time
                )
            )
            PaymentScreen(
                onBack = { navController.popBackStack() },
                onComplete = { bookingId ->
                    navController.navigate(
                        Screen.BookingSuccess.createRoute(
                            providerId = providerId,
                            serviceId = serviceId,
                            date = dateString,
                            time = timeString,
                            bookingId = bookingId
                        )
                    ) {
                        popUpTo(Screen.SelectDateTime.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
