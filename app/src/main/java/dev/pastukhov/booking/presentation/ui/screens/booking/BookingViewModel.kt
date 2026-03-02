package dev.pastukhov.booking.presentation.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.local.dao.BookingDao
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mapper.toEntity
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import dev.pastukhov.booking.domain.model.PaymentMethod
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.model.TimeSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    /**
     * Initialize booking with provider and service data.
     */
    fun initializeBooking(providerId: String, serviceId: String) {
        // Get provider and service from mock data (in real app, fetch from repository)
        val provider = MockData.mockProviders.find { it.id == providerId }
        val service = MockData.getServicesForProvider(providerId).find { it.id == serviceId }

        if (provider != null && service != null) {
            _uiState.update {
                it.copy(
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSlots = true) }

            // Simulate network delay
            kotlinx.coroutines.delay(500)

            // Generate mock time slots (9:00 - 18:00, 30-minute intervals)
            val slots = generateTimeSlots(date)

            _uiState.update {
                it.copy(
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
        _uiState.update {
            it.copy(
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
        _uiState.update {
            it.copy(
                selectedTime = time,
                timeError = null
            )
        }
    }

    /**
     * Update notes for the booking.
     */
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    /**
     * Update user phone number.
     */
    fun updatePhone(phone: String) {
        _uiState.update {
            it.copy(
                userPhone = phone,
                phoneError = if (phone.length < 10) "Phone number is too short" else null
            )
        }
    }

    /**
     * Select payment method.
     */
    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    /**
     * Update card details.
     */
    fun updateCardNumber(number: String) {
        val filtered = number.filter { it.isDigit() }.take(16)
        _uiState.update {
            it.copy(
                cardNumber = filtered,
                cardNumberError = if (filtered.length < 16) "Invalid card number" else null
            )
        }
    }

    fun updateCardExpiry(expiry: String) {
        val filtered = expiry.filter { it.isDigit() }.take(4)
        _uiState.update {
            it.copy(
                cardExpiry = filtered,
                cardExpiryError = if (filtered.length < 4) "Invalid expiry" else null
            )
        }
    }

    fun updateCardCvv(cvv: String) {
        val filtered = cvv.filter { it.isDigit() }.take(3)
        _uiState.update {
            it.copy(
                cardCvv = filtered,
                cardCvvError = if (filtered.length < 3) "Invalid CVV" else null
            )
        }
    }

    /**
     * Validate current step and proceed to next.
     */
    fun proceedToNextStep() {
        val currentState = _uiState.value

        when (currentState.currentStep) {
            BookingStep.SELECT_DATETIME -> {
                if (currentState.canProceedToConfirmation) {
                    _uiState.update { it.copy(currentStep = BookingStep.CONFIRMATION) }
                }
            }
            BookingStep.CONFIRMATION -> {
                if (currentState.canProceedToPayment) {
                    _uiState.update { it.copy(currentStep = BookingStep.PAYMENT) }
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
        val currentState = _uiState.value

        when (currentState.currentStep) {
            BookingStep.SELECT_DATETIME -> {
                // Can't go back from first step
            }
            BookingStep.CONFIRMATION -> {
                _uiState.update { it.copy(currentStep = BookingStep.SELECT_DATETIME) }
            }
            BookingStep.PAYMENT -> {
                _uiState.update { it.copy(currentStep = BookingStep.CONFIRMATION) }
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val provider = state.provider ?: return@launch
            val service = state.service ?: return@launch
            val date = state.selectedDate ?: return@launch
            val time = state.selectedTime ?: return@launch

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
                notes = state.notes.ifBlank { null },
                paymentMethod = state.selectedPaymentMethod,
                cardNumber = if (state.selectedPaymentMethod == PaymentMethod.CARD)
                    state.cardNumber else null,
                cardExpiry = if (state.selectedPaymentMethod == PaymentMethod.CARD)
                    state.cardExpiry else null,
                isPaid = true // In real app, would verify payment
            )

            // Save to database
            try {
                bookingDao.insertBooking(booking.toEntity())

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        completedBooking = booking,
                        isBookingComplete = true,
                        currentStep = BookingStep.SUCCESS
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save booking: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Reset booking flow.
     */
    fun resetBooking() {
        _uiState.value = BookingUiState()
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
