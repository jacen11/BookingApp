package dev.pastukhov.booking.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.presentation.model.BookingSuccessEvent
import dev.pastukhov.booking.presentation.model.PaymentEvent
import dev.pastukhov.booking.presentation.ui.screens.ProviderDetailScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingConfirmationScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingConfirmationViewModel
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingSuccessScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.PaymentScreen
import dev.pastukhov.booking.presentation.ui.screens.booking.SelectDateTimeScreen
import dev.pastukhov.booking.presentation.ui.screens.bookingdetails.BookingDetailsScreen
import dev.pastukhov.booking.presentation.ui.screens.home.HomeScreen
import dev.pastukhov.booking.presentation.ui.screens.login.LoginScreen
import dev.pastukhov.booking.presentation.ui.screens.mybookings.MyBookingsScreen
import dev.pastukhov.booking.presentation.ui.screens.profile.ProfileScreen
import dev.pastukhov.booking.presentation.ui.screens.search.SearchScreen
import dev.pastukhov.booking.presentation.ui.screens.splash.SplashScreen
import dev.pastukhov.booking.presentation.viewmodel.BookingSuccessViewModel
import dev.pastukhov.booking.presentation.viewmodel.PaymentViewModel
import dev.pastukhov.booking.presentation.viewmodel.SelectDateTimeViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

/**
 * Main Navigation Host for the app.
 * Defines navigation routes and screens with bottom navigation.
 */
@Composable
fun BookingNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    //todo transfer to viewmodel
    userSettingsRepository: UserSettingsRepository,
    startDestination: String = Screen.Splash.route
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
            // Splash Screen
            composable(route = Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Login Screen
            composable(route = Screen.Login.route) {
                LoginScreen(
                    //todo transfer to viewModel
                    userSettingsRepository = userSettingsRepository,
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
                            //todo throw error
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

            // Booking Success Screen
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

            // My Bookings Screen
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

            // Booking Detail Screen - with Deep Link support
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
}
