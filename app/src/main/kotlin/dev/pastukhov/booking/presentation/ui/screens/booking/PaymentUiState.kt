package dev.pastukhov.booking.presentation.ui.screens.booking

import dev.pastukhov.booking.domain.model.PaymentMethod
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI State for Payment Screen.
 */
data class PaymentUiState(
    val provider: Provider? = null,
    val service: Service? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val notes: String = "",
    val userPhone: String = "",
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CARD,
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val cardCvv: String = "",
    val cardNumberError: String? = null,
    val cardExpiryError: String? = null,
    val cardCvvError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isCardValid: Boolean
        get() = when (selectedPaymentMethod) {
            PaymentMethod.CARD -> {
                cardNumber.length >= 16 &&
                cardExpiry.length >= 4 &&
                cardCvv.length >= 3
            }
            else -> true
        }

    val canCompleteBooking: Boolean
        get() = isCardValid && cardNumberError == null && cardExpiryError == null && cardCvvError == null
}
