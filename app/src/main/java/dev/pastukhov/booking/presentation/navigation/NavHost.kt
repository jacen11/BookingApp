package dev.pastukhov.booking.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import dev.pastukhov.booking.presentation.ui.screens.HomeScreen
import dev.pastukhov.booking.presentation.ui.screens.LoginScreen
import dev.pastukhov.booking.presentation.ui.screens.ProviderDetailScreen
import dev.pastukhov.booking.presentation.ui.screens.profile.ProfileScreen
import dev.pastukhov.booking.presentation.ui.screens.search.SearchScreen

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

            // Bookings Screen
            composable(route = "bookings") {
                // TODO: Create BookingsScreen
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
            ) { backStackEntry ->
                val providerId = backStackEntry.arguments?.getString("providerId") ?: return@composable
                ProviderDetailScreen(
                    providerId = providerId,
                    onBookingClick = { providerId, serviceId ->
                        navController.navigate(Screen.SelectDateTime.createRoute(providerId, serviceId))
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
                val providerId = backStackEntry.arguments?.getString("providerId") ?: return@composable
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
                val providerId = backStackEntry.arguments?.getString("providerId") ?: return@composable
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: return@composable
                // TODO: Create SelectDateTimeScreen
            }

            // Confirm Booking Screen
            composable(route = Screen.ConfirmBooking.route) {
                // TODO: Create BookingConfirmationScreen
            }

            // My Bookings Screen
            composable(route = Screen.MyBookings.route) {
                // TODO: Create MyBookingsScreen
            }

            // Booking Detail Screen
            composable(
                route = Screen.BookingDetail.route,
                arguments = listOf(
                    navArgument("bookingId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: return@composable
                // TODO: Create BookingDetailScreen
            }
        }
    }
}
