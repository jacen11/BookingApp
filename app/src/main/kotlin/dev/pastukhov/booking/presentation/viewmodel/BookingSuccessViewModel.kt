package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.usecase.GetProvidersUseCase
import dev.pastukhov.booking.domain.usecase.GetServicesUseCase
import dev.pastukhov.booking.presentation.model.BookingSuccessEvent
import dev.pastukhov.booking.presentation.model.BookingSuccessUiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Booking Success Screen.
 * Displays successful booking confirmation.
 */
@HiltViewModel
class BookingSuccessViewModel @Inject constructor(
    private val providersUseCase: GetProvidersUseCase,
    private val servicesUseCase: GetServicesUseCase
) : BaseViewModel<BookingSuccessUiState, BookingSuccessEvent>() {

    override fun initialState(): BookingSuccessUiState = BookingSuccessUiState()

    override fun handleEvent(event: BookingSuccessEvent) {
        when (event) {
            is BookingSuccessEvent.InitializeBooking -> initializeBooking(
                event.providerId,
                event.serviceId,
                event.date,
                event.time,
                event.bookingId
            )
            is BookingSuccessEvent.ClearError -> clearError()
            is BookingSuccessEvent.ResetBooking -> resetBooking()
        }
    }

    /**
     * Initialize success screen with booking data.
     */
    private fun initializeBooking(
        providerId: String,
        serviceId: String,
        date: java.time.LocalDate,
        time: java.time.LocalTime,
        bookingId: String
    ) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            combine(
                providersUseCase.byId(providerId),
                servicesUseCase.byId(serviceId)
            ) { provider, service ->
                Pair(provider, service)
            }
                .catch { e ->
                    updateState {
                        copy(isLoading = false, error = e.message)
                    }
                }
                .collect { (provider, service) ->
                    updateState {
                        copy(
                            provider = provider,
                            service = service,
                            selectedDate = date,
                            selectedTime = time,
                            bookingId = bookingId,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * Clear error message.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }

    /**
     * Reset the success screen state.
     */
    private fun resetBooking() {
        updateState { BookingSuccessUiState() }
    }
}
