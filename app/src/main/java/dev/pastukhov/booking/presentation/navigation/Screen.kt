package dev.pastukhov.booking.presentation.navigation

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

    data object ConfirmBooking : Screen("booking/confirm") {
        const val ARG_PROVIDER_ID = "providerId"
        const val ARG_SERVICE_ID = "serviceId"
        const val ARG_DATE = "date"
        const val ARG_TIME = "time"
    }

    // Profile screens
    data object Profile : Screen("profile")
    data object MyBookings : Screen("profile/bookings")
    data object BookingDetail : Screen("booking/{bookingId}") {
        fun createRoute(bookingId: String) = "booking/$bookingId"
    }
}

/**
 * Bottom navigation items.
 */
enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: String
) {
    HOME("home", "Inicio", "home"),
    BOOKINGS("profile/bookings", "Citas", "calendar"),
    PROFILE("profile", "Perfil", "person")
}
