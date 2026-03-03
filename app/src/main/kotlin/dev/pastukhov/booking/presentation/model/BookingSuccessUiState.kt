package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI State for Booking Success Screen.
 */
data class BookingSuccessUiState(
    val provider: Provider? = null,
    val service: Service? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val bookingId: String = "",
    val error: String? = null
)