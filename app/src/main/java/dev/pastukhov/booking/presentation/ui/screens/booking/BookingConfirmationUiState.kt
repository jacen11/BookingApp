package dev.pastukhov.booking.presentation.ui.screens.booking

import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI State for Booking Confirmation Screen.
 */
data class BookingConfirmationUiState(
    val provider: Provider? = null,
    val service: Service? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val notes: String = "",
    val userPhone: String = "",
    val phoneError: String? = null,
    val error: String? = null
) {
    val isValid: Boolean
        get() = userPhone.isNotBlank() && userPhone.length >= 10 && phoneError == null
}
