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
import java.util.UUID

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
            val date = backStackEntry.arguments?.getString("date") ?: return@composable
            val time = backStackEntry.arguments?.getString("time") ?: return@composable

            val viewModel: PaymentViewModel = hiltViewModel()
            viewModel.handleEvent(
                PaymentEvent.InitializePayment(
                    providerId = providerId,
                    serviceId = serviceId,
                    date = LocalDate.parse(date),
                    time = LocalTime.parse(time)
                )
            )
            PaymentScreen(
                onBack = { navController.popBackStack() },
                onComplete = {
                    val bookingId = viewModel.state.value.bookingId.ifEmpty {
                        UUID.randomUUID().toString()
                    }
                    navController.navigate(
                        Screen.BookingSuccess.createRoute(
                            providerId = providerId,
                            serviceId = serviceId,
                            date = date,
                            time = time,
                            bookingId = bookingId
                        )
                    ) {
                        popUpTo(Screen.SelectDateTime.route) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
