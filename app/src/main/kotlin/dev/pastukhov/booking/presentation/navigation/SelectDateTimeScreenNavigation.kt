package dev.pastukhov.booking.presentation.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.pastukhov.booking.presentation.ui.screens.booking.SelectDateTimeScreen
import dev.pastukhov.booking.presentation.viewmodel.SelectDateTimeViewModel

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

            val selectDateTimeViewModel: SelectDateTimeViewModel = hiltViewModel()

            SelectDateTimeScreen(
                providerId = providerId,
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onNext = {
                    val date = selectDateTimeViewModel.state.value.selectedDate
                    val time = selectDateTimeViewModel.state.value.selectedTime
                    if (date != null && time != null) {
                        navController.navigate(
                            Screen.ConfirmBooking.createRoute(
                                providerId = providerId,
                                serviceId = serviceId,
                                date = date.toString(),
                                time = time.toString()
                            )
                        )
                    }
                },
                viewModel = selectDateTimeViewModel
            )
        }
    }
}
