package dev.pastukhov.booking.presentation.ui.screens.booking

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.local.dao.BookingDao
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mapper.toEntity
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import dev.pastukhov.booking.domain.model.PaymentMethod
import dev.pastukhov.booking.presentation.viewmodel.BaseViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for Payment Screen.
 * Handles payment processing and booking completion.
 */
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val bookingDao: BookingDao
) : BaseViewModel<PaymentUiState, Any>() {

    override fun initialState(): PaymentUiState = PaymentUiState()

    /**
     * Initialize payment with booking data.
     */
    fun initializePayment(providerId: String, serviceId: String, date: LocalDate, time: LocalTime) {
        val provider = MockData.mockProviders.find { it.id == providerId }
        val service = MockData.getServicesForProvider(providerId).find { it.id == serviceId }

        updateState {
            copy(
                provider = provider?.toDomain(),
                service = service?.toDomain(),
                selectedDate = date,
                selectedTime = time
            )
        }
    }

    /**
     * Set confirmation data from previous screen.
     */
    fun setConfirmationData(notes: String, phone: String) {
        updateState {
            copy(
                notes = notes,
                userPhone = phone
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
     * Complete the booking and save to database.
     */
    fun completeBooking(onSuccess: () -> Unit) {
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

            val booking = Booking(
                id = UUID.randomUUID().toString(),
                userId = "current_user_id",
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
                isPaid = true
            )

            bookingDao.insertBooking(booking.toEntity())

            updateState {
                copy(
                    isLoading = false,
                    error = null
                )
            }

            onSuccess()
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        updateState { copy(error = null) }
    }
}
