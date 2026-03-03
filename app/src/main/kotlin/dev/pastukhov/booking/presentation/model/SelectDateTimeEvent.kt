package dev.pastukhov.booking.presentation.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Events for Select Date & Time screen.
 */
sealed class SelectDateTimeEvent {
    data class InitializeBooking(
        val providerId: String,
        val serviceId: String
    ) : SelectDateTimeEvent()

    data class LoadTimeSlots(
        val date: LocalDate = LocalDate.now()
    ) : SelectDateTimeEvent()

    data class SelectDate(val date: LocalDate) : SelectDateTimeEvent()

    data class SelectTime(val time: LocalTime) : SelectDateTimeEvent()

    data object ClearError : SelectDateTimeEvent()
}
