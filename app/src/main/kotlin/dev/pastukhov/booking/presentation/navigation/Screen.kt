package dev.pastukhov.booking.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import dev.pastukhov.booking.R

/**
 * Sealed class representing all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    // Splash screen
    data object Splash : Screen("splash")

    // Auth screens
    data object Login : Screen("login")
    data object Register : Screen("register")

    // Main screens
    data object Home : Screen("home")

    // Service/Provider screens
    data object ServiceDetail : Screen("service/{providerId}") {
        fun createRoute(providerId: String) = "service/$providerId"
    }

    // Booking screens
    data object SelectService : Screen("booking/{providerId}/services") {
        fun createRoute(providerId: String) = "booking/$providerId/services"
    }

    data object SelectDateTime : Screen("booking/{providerId}/datetime/{serviceId}") {
        fun createRoute(providerId: String, serviceId: String) =
            "booking/$providerId/datetime/$serviceId"
    }

    data object ConfirmBooking : Screen("booking/confirm/{providerId}/{serviceId}/{date}/{time}") {
        const val ARG_PROVIDER_ID = "providerId"
        const val ARG_SERVICE_ID = "serviceId"
        const val ARG_DATE = "date"
        const val ARG_TIME = "time"

        fun createRoute(providerId: String, serviceId: String, date: String, time: String) =
            "booking/confirm/$providerId/$serviceId/$date/$time"
    }

    // Payment screen
    data object Payment : Screen("booking/payment/{providerId}/{serviceId}/{date}/{time}") {
        fun createRoute(providerId: String, serviceId: String, date: String, time: String) =
            "booking/payment/$providerId/$serviceId/$date/$time"
    }

    // Booking success screen
    data object BookingSuccess : Screen("booking/success")

    // Profile screens
    data object Profile : Screen("profile")
    data object MyBookings : Screen("bookings")
    data object BookingDetail : Screen("booking/{bookingId}") {
        fun createRoute(bookingId: String) = "booking/$bookingId"
    }
}

/**
 * Bottom navigation items.
 */
enum class BottomNavItem(
    val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
) {
    HOME("home", R.string.nav_home, Icons.Default.Home),
    SEARCH("search", R.string.nav_search, Icons.Default.Search),
    BOOKINGS("bookings", R.string.nav_bookings, Icons.Default.CalendarToday),
    PROFILE("profile", R.string.nav_profile, Icons.Default.Person)
}
