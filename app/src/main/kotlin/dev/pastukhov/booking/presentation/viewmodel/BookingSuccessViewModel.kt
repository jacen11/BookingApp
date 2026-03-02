package dev.pastukhov.booking.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.presentation.ui.screens.booking.BookingSuccessUiState
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel for Booking Success Screen.
 * Displays successful booking confirmation.
 */
@HiltViewModel
class BookingSuccessViewModel @Inject constructor() : BaseViewModel<BookingSuccessUiState, Any>() {

    override fun initialState(): BookingSuccessUiState = BookingSuccessUiState()

    override fun handleEvent(event: Any) {
        TODO("Not yet implemented")
    }

    /**
     * Initialize success screen with booking data.
     */
    fun initializeBooking(
        providerId: String,
        serviceId: String,
        date: LocalDate,
        time: LocalTime,
        bookingId: String
    ) {
        val provider = MockData.mockProviders.find { it.id == providerId }
        val service = MockData.getServicesForProvider(providerId).find { it.id == serviceId }

        updateState {
            copy(
                provider = provider?.toDomain(),
                service = service?.toDomain(),
                selectedDate = date,
                selectedTime = time,
                bookingId = bookingId
            )
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        updateState { copy(error = null) }
    }

    /**
     * Reset the success screen state.
     */
    fun resetBooking() {
        updateState { BookingSuccessUiState() }
    }
}