package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.PaymentMethod
import java.time.LocalDate
import java.time.LocalTime

/**
 * Events for Payment screen.
 */
sealed class PaymentEvent {
    data class InitializePayment(
        val providerId: String,
        val serviceId: String,
        val date: LocalDate,
        val time: LocalTime
    ) : PaymentEvent()

    data class SetConfirmationData(
        val notes: String,
        val phone: String
    ) : PaymentEvent()

    data class SelectPaymentMethod(val method: PaymentMethod) : PaymentEvent()

    data class UpdateCardNumber(val number: String) : PaymentEvent()

    data class UpdateCardExpiry(val expiry: String) : PaymentEvent()

    data class UpdateCardCvv(val cvv: String) : PaymentEvent()

    data object CompleteBooking : PaymentEvent()

    data object ClearError : PaymentEvent()
}
