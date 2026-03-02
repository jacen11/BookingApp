package dev.pastukhov.booking.presentation.ui.screens.booking

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.presentation.viewmodel.BaseViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel for Booking Confirmation Screen.
 * Manages booking details confirmation.
 */
@HiltViewModel
class BookingConfirmationViewModel @Inject constructor() : BaseViewModel<BookingConfirmationUiState, Any>() {

    override fun initialState(): BookingConfirmationUiState = BookingConfirmationUiState()

    /**
     * Set booking data from navigation arguments.
     */
    fun initializeBooking(
        providerId: String,
        serviceId: String,
        date: LocalDate,
        time: LocalTime
    ) {
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
     * Clear error message.
     */
    fun clearError() {
        updateState { copy(error = null) }
    }
}
