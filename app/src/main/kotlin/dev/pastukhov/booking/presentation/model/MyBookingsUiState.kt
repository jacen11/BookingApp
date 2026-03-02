package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Booking

/**
 * UI State for My Bookings screen.
 */
data class MyBookingsUiState(
    val isLoading: Boolean = true,
    val activeBookings: List<Booking> = emptyList(),
    val completedBookings: List<Booking> = emptyList(),
    val cancelledBookings: List<Booking> = emptyList(),
    val selectedTab: BookingTab = BookingTab.ACTIVE,
    val error: String? = null,
    val showCancelDialog: Boolean = false,
    val bookingToCancel: Booking? = null,
    val isCancelling: Boolean = false
) {
    /**
     * Returns bookings for the currently selected tab.
     */
    val currentBookings: List<Booking>
        get() = when (selectedTab) {
            BookingTab.ACTIVE -> activeBookings
            BookingTab.HISTORY -> completedBookings
            BookingTab.CANCELLED -> cancelledBookings
        }

    /**
     * Check if the current tab has no bookings.
     */
    val isEmpty: Boolean
        get() = currentBookings.isEmpty() && !isLoading && error == null
}

/**
 * Tab selection for My Bookings.
 */
enum class BookingTab {
    ACTIVE,    // Pending + Confirmed
    HISTORY,   // Completed
    CANCELLED  // Cancelled + NoShow
}

/**
 * Extension to filter bookings by tab.
 */
fun List<Booking>.filterByTab(tab: BookingTab): List<Booking> {
    return when (tab) {
        BookingTab.ACTIVE -> filter { it.status == dev.pastukhov.booking.domain.model.BookingStatus.PENDING || it.status == dev.pastukhov.booking.domain.model.BookingStatus.CONFIRMED }
        BookingTab.HISTORY -> filter { it.status == dev.pastukhov.booking.domain.model.BookingStatus.COMPLETED }
        BookingTab.CANCELLED -> filter { it.status == dev.pastukhov.booking.domain.model.BookingStatus.CANCELLED || it.status == dev.pastukhov.booking.domain.model.BookingStatus.NO_SHOW }
    }
}
