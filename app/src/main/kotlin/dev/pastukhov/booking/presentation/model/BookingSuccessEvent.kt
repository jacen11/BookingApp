package dev.pastukhov.booking.presentation.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Events for Booking Success Screen.
 */
sealed class BookingSuccessEvent {
    data class InitializeBooking(
        val providerId: String,
        val serviceId: String,
        val date: LocalDate,
        val time: LocalTime,
        val bookingId: String
    ) : BookingSuccessEvent()

    data object ClearError : BookingSuccessEvent()

    data object ResetBooking : BookingSuccessEvent()
}
