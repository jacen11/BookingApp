package dev.pastukhov.booking.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import dev.pastukhov.booking.presentation.ui.screens.ProviderDetailScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingConfirmationScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingConfirmationViewModel
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingSuccessScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.PaymentScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.SelectDateTimeScreen
import dev.pastukhov.booking.presentation.ui.screens.home.HomeScreen
import dev.pastukhov.booking.presentation.ui.screens.login.LoginScreen
import dev.pastukhov.booking.presentation.ui.screens.mybookings.MyBookingsScreen
import dev.pastukhov.booking.presentation.ui.screens.profile.ProfileScreen
import dev.pastukhov.booking.presentation.ui.screens.search.SearchScreen
import dev.pastukhov.booking.presentation.viewmodel.PaymentViewModel
import dev.pastukhov.booking.presentation.viewmodel.SelectDateTimeViewModel

/**
 * Main Navigation Host for the app.
 * Defines navigation routes and screens with bottom navigation.
 */
@Composable
fun BookingNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(paddingValues)
        ) {
            // Login Screen
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Home Screen
            composable(route = Screen.Home.route) {
                HomeScreen(
                    onProviderClick = { providerId ->
                        navController.navigate(Screen.ServiceDetail.createRoute(providerId))
                    }
                )
            }

            // Search Screen
            composable(route = "search") {
                SearchScreen(
                    onProviderClick = { providerId ->
                        navController.navigate(Screen.ServiceDetail.createRoute(providerId))
                    }
                )
            }

            // Profile Screen
            composable(route = Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToBookings = {
                        navController.navigate(Screen.MyBookings.route)
                    }
                )
            }

            // Provider Detail Screen (Service Detail)
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

            // Select Service Screen
            composable(
                route = Screen.SelectService.route,
                arguments = listOf(
                    navArgument("providerId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val providerId =
                    backStackEntry.arguments?.getString("providerId") ?: return@composable
                // TODO: Create SelectServiceScreen
            }

            // Select DateTime Screen
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

            // Confirm Booking Screen
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
                    date = java.time.LocalDate.parse(date),
                    time = java.time.LocalTime.parse(time)
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

            // Payment Screen
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
                viewModel.initializePayment(
                    providerId = providerId,
                    serviceId = serviceId,
                    date = java.time.LocalDate.parse(date),
                    time = java.time.LocalTime.parse(time)
                )

                PaymentScreen(
                    onBack = { navController.popBackStack() },
                    onComplete = {
                        navController.navigate(Screen.BookingSuccess.route) {
                            popUpTo(Screen.SelectDateTime.route) { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }

            // Booking Success Screen
            composable(route = Screen.BookingSuccess.route) {
                BookingSuccessScreen(
                    onViewBookings = {
                        navController.navigate(Screen.MyBookings.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onDone = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // My Bookings Screen
            composable(route = Screen.MyBookings.route) {
                MyBookingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onBookNow = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.MyBookings.route)
                        }
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

            // Booking Detail Screen
            composable(
                route = Screen.BookingDetail.route,
                arguments = listOf(
                    navArgument("bookingId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookingId =
                    backStackEntry.arguments?.getString("bookingId") ?: return@composable
                // TODO: Create BookingDetailScreen
            }
        }
    }
}
