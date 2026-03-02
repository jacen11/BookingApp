package dev.pastukhov.booking.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.local.dao.BookingDao
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mapper.toEntity
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import dev.pastukhov.booking.domain.model.PaymentMethod
import dev.pastukhov.booking.domain.model.TimeSlot
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingStep
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingUiState
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the entire booking flow.
 * Manages state across all 4 booking screens.
 */
@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingDao: BookingDao
) : BaseViewModel<BookingUiState, Any>() {

    override fun initialState(): BookingUiState = BookingUiState()

    /**
     * Initialize booking with provider and service data.
     */
    fun initializeBooking(providerId: String, serviceId: String) {
        // Get provider and service from mock data (in real app, fetch from repository)
        val provider = MockData.mockProviders.find { it.id == providerId }
        val service = MockData.getServicesForProvider(providerId).find { it.id == serviceId }

        if (provider != null && service != null) {
            updateState {
                copy(
                    provider = provider.toDomain(),
                    service = service.toDomain(),
                    currentStep = BookingStep.SELECT_DATETIME
                )
            }
            // Load available time slots
            loadTimeSlots()
        }
    }

    /**
     * Generate mock available time slots for a given date.
     * In a real app, this would come from the backend.
     */
    fun loadTimeSlots(date: LocalDate = LocalDate.now()) {
        launchWithErrorHandling(
            onError = { throwable ->
                updateState { copy(error = throwable.message, isLoadingSlots = false) }
            }
        ) {
            updateState { copy(isLoadingSlots = true) }

            // Simulate network delay
            delay(500)

            // Generate mock time slots (9:00 - 18:00, 30-minute intervals)
            val slots = generateTimeSlots(date)

            updateState {
                copy(
                    availableTimeSlots = slots,
                    isLoadingSlots = false
                )
            }
        }
    }

    /**
     * Generate time slots with some randomly marked as unavailable.
     */
    private fun generateTimeSlots(date: LocalDate): List<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()
        val startHour = 9
        val endHour = 18

        // Use date to generate consistent "unavailable" slots
        val seed = date.dayOfMonth + date.monthValue

        for (hour in startHour until endHour) {
            for (minute in listOf(0, 30)) {
                val startTime = LocalTime.of(hour, minute)
                val endTime = startTime.plusMinutes(30)

                // Mock: slots at 10:00, 11:30, 14:00 are unavailable
                val isUnavailable = hour in listOf(10, 11, 14) && minute in listOf(0, 30)

                slots.add(
                    TimeSlot(
                        startTime = startTime,
                        endTime = endTime,
                        isAvailable = !isUnavailable
                    )
                )
            }
        }

        return slots
    }

    /**
     * Select a date for the booking.
     */
    fun selectDate(date: LocalDate) {
        updateState {
            copy(
                selectedDate = date,
                selectedTime = null,
                dateError = null
            )
        }
        loadTimeSlots(date)
    }

    /**
     * Select a time slot.
     */
    fun selectTime(time: LocalTime) {
        updateState {
            copy(
                selectedTime = time,
                timeError = null
            )
        }
    }

    /**
     * Update notes for the booking.
     */
    fun updateNotes(notes: String) {
        updateState { copy(notes = notes) }
    }

    /**
     * Update user phone number.
     */
    fun updatePhone(phone: String) {
        updateState {
            copy(
                userPhone = phone,
                phoneError = if (phone.length < 10) "Phone number is too short" else null
            )
        }
    }

    /**
     * Select payment method.
     */
    fun selectPaymentMethod(method: PaymentMethod) {
        updateState { copy(selectedPaymentMethod = method) }
    }

    /**
     * Update card details.
     */
    fun updateCardNumber(number: String) {
        val filtered = number.filter { it.isDigit() }.take(16)
        updateState {
            copy(
                cardNumber = filtered,
                cardNumberError = if (filtered.length < 16) "Invalid card number" else null
            )
        }
    }

    fun updateCardExpiry(expiry: String) {
        val filtered = expiry.filter { it.isDigit() }.take(4)
        updateState {
            copy(
                cardExpiry = filtered,
                cardExpiryError = if (filtered.length < 4) "Invalid expiry" else null
            )
        }
    }

    fun updateCardCvv(cvv: String) {
        val filtered = cvv.filter { it.isDigit() }.take(3)
        updateState {
            copy(
                cardCvv = filtered,
                cardCvvError = if (filtered.length < 3) "Invalid CVV" else null
            )
        }
    }

    /**
     * Validate current step and proceed to next.
     */
    fun proceedToNextStep() {
        val currentState = state.value

        when (currentState.currentStep) {
            BookingStep.SELECT_DATETIME -> {
                if (currentState.canProceedToConfirmation) {
                    updateState { copy(currentStep = BookingStep.CONFIRMATION) }
                }
            }
            BookingStep.CONFIRMATION -> {
                if (currentState.canProceedToPayment) {
                    updateState { copy(currentStep = BookingStep.PAYMENT) }
                }
            }
            BookingStep.PAYMENT -> {
                if (currentState.canCompleteBooking) {
                    completeBooking()
                }
            }
            BookingStep.SUCCESS -> {
                // Already complete, do nothing
            }
        }
    }

    /**
     * Go back to previous step.
     */
    fun goBack() {
        val currentState = state.value

        when (currentState.currentStep) {
            BookingStep.SELECT_DATETIME -> {
                // Can't go back from first step
            }
            BookingStep.CONFIRMATION -> {
                updateState { copy(currentStep = BookingStep.SELECT_DATETIME) }
            }
            BookingStep.PAYMENT -> {
                updateState { copy(currentStep = BookingStep.CONFIRMATION) }
            }
            BookingStep.SUCCESS -> {
                // Can't go back from success
            }
        }
    }

    /**
     * Complete the booking and save to database.
     */
    private fun completeBooking() {
        launchWithErrorHandling(
            onError = { throwable ->
                updateState { copy(isLoading = false, error = "Failed to save booking: ${throwable.message}") }
            }
        ) {
            updateState { copy(isLoading = true) }

            val currentState = state.value
            val provider = currentState.provider ?: return@launchWithErrorHandling
            val service = currentState.service ?: return@launchWithErrorHandling
            val date = currentState.selectedDate ?: return@launchWithErrorHandling
            val time = currentState.selectedTime ?: return@launchWithErrorHandling

            // Create booking object
            val booking = Booking(
                id = UUID.randomUUID().toString(),
                userId = "current_user_id", // In real app, get from auth
                providerId = provider.id,
                providerName = provider.name,
                providerAddress = provider.address,
                serviceId = service.id,
                serviceName = service.name,
                date = date,
                time = time,
                status = BookingStatus.PENDING,
                totalPrice = service.price,
                notes = currentState.notes.ifBlank { null },
                paymentMethod = currentState.selectedPaymentMethod,
                cardNumber = if (currentState.selectedPaymentMethod == PaymentMethod.CARD)
                    currentState.cardNumber else null,
                cardExpiry = if (currentState.selectedPaymentMethod == PaymentMethod.CARD)
                    currentState.cardExpiry else null,
                isPaid = true // In real app, would verify payment
            )

            // Save to database
            bookingDao.insertBooking(booking.toEntity())

            updateState {
                copy(
                    isLoading = false,
                    completedBooking = booking,
                    isBookingComplete = true,
                    currentStep = BookingStep.SUCCESS
                )
            }
        }
    }

    /**
     * Reset booking flow.
     */
    fun resetBooking() {
        updateState { BookingUiState() }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        updateState { copy(error = null) }
    }
}