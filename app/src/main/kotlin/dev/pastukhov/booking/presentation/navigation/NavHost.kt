package dev.pastukhov.booking.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.pastukhov.booking.data.repository.UserSettingsRepository

/**
 * Main Navigation Host for the app.
 * Defines navigation routes and screens with bottom navigation.
 * Uses unified NavGraphBuilder.createNavGraph() approach for all screens.
 */
@Composable
fun BookingNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
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
            SplashScreenNavigation.run { createNavGraph(navController) }

            // Login Screen
            LoginScreenNavigation.run { createNavGraph(navController, userSettingsRepository) }

            // Home Screen
            HomeScreenNavigation.run { createNavGraph(navController) }

            // Search Screen
            SearchScreenNavigation.run { createNavGraph(navController) }

            // Profile Screen
            ProfileScreenNavigation.run { createNavGraph(navController) }

            // Provider Detail Screen (Service Detail)
            ServiceDetailScreenNavigation.run { createNavGraph(navController) }

            // Select DateTime Screen
            SelectDateTimeScreenNavigation.run { createNavGraph(navController) }

            // Confirm Booking Screen
            ConfirmBookingScreenNavigation.run { createNavGraph(navController) }

            // Payment Screen
            PaymentScreenNavigation.run { createNavGraph(navController) }

            // Booking Success Screen
            BookingSuccessScreenNavigation.run { createNavGraph(navController) }

            // My Bookings Screen
            MyBookingsScreenNavigation.run { createNavGraph(navController) }

            // Booking Detail Screen
            BookingDetailScreenNavigation.run { createNavGraph(navController) }
        }
    }
}
