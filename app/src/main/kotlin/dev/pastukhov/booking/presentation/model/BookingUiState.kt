package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.PaymentMethod
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.model.TimeSlot
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI State for the entire booking flow.
 * Contains all data needed across the 4 booking screens.
 */
data class BookingUiState(
    // Navigation step
    val currentStep: BookingStep = BookingStep.SELECT_DATETIME,

    // Provider and Service (passed from previous screen)
    val provider: Provider? = null,
    val service: Service? = null,

    // Step 1: Date & Time
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val availableTimeSlots: List<TimeSlot> = emptyList(),
    val isLoadingSlots: Boolean = false,

    // Step 2: Confirmation
    val notes: String = "",
    val userPhone: String = "",

    // Step 3: Payment
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CARD,
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val cardCvv: String = "",

    // Validation errors
    val dateError: String? = null,
    val timeError: String? = null,
    val phoneError: String? = null,
    val cardNumberError: String? = null,
    val cardExpiryError: String? = null,
    val cardCvvError: String? = null,

    // Loading and error states
    val isLoading: Boolean = false,
    val error: String? = null,

    // Success state
    val completedBooking: Booking? = null,
    val isBookingComplete: Boolean = false
) {
    /**
     * Check if date and time are selected.
     */
    val isDateTimeSelected: Boolean
        get() = selectedDate != null && selectedTime != null

    /**
     * Check if confirmation is valid.
     */
    val isConfirmationValid: Boolean
        get() = userPhone.isNotBlank() && userPhone.length >= 10

    /**
     * Check if card details are valid for CARD payment.
     */
    val isCardValid: Boolean
        get() = when (selectedPaymentMethod) {
            PaymentMethod.CARD -> {
                cardNumber.length >= 16 &&
                cardExpiry.length >= 4 &&
                cardCvv.length >= 3
            }
            else -> true
        }

    /**
     * Check if can proceed to next step.
     */
    val canProceedToConfirmation: Boolean
        get() = isDateTimeSelected && dateError == null && timeError == null

    /**
     * Check if can proceed to payment.
     */
    val canProceedToPayment: Boolean
        get() = isConfirmationValid && phoneError == null

    /**
     * Check if can complete booking.
     */
    val canCompleteBooking: Boolean
        get() = canProceedToPayment && isCardValid

    /**
     * Get minimum selectable date (today + 1 day).
     */
    fun getMinDate(): LocalDate = LocalDate.now().plusDays(1)

    /**
     * Get maximum selectable date (today + 30 days).
     */
    fun getMaxDate(): LocalDate = LocalDate.now().plusDays(30)
}

/**
 * Booking flow steps.
 */
enum class BookingStep {
    SELECT_DATETIME,    // Screen 1: Select Date & Time
    CONFIRMATION,      // Screen 2: Booking Confirmation
    PAYMENT,           // Screen 3: Payment
    SUCCESS            // Screen 4: Success
}
