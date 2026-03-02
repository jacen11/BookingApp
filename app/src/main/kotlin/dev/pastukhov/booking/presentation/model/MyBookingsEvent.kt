package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Booking

/**
 * Events for MyBookings screen.
 */
sealed class MyBookingsEvent {
    data object LoadBookings : MyBookingsEvent()
    data class SelectTab(val tab: BookingTab) : MyBookingsEvent()
    data class ShowCancelDialog(val booking: Booking) : MyBookingsEvent()
    data object DismissCancelDialog : MyBookingsEvent()
    data object ConfirmCancelBooking : MyBookingsEvent()
    data object ClearError : MyBookingsEvent()
}
