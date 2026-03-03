package dev.pastukhov.booking.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.repository.BookingRepository
import dev.pastukhov.booking.domain.repository.ProviderRepository
import dev.pastukhov.booking.domain.repository.ServiceRepository
import dev.pastukhov.booking.presentation.model.BookingDetailsData
import dev.pastukhov.booking.presentation.model.BookingDetailsEvent
import dev.pastukhov.booking.presentation.model.BookingDetailsUiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * ViewModel for Booking Details screen.
 * Handles loading booking details, provider, and service information.
 * Manages cancellation and rating actions.
 */
@HiltViewModel
class BookingDetailsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val providerRepository: ProviderRepository,
    private val serviceRepository: ServiceRepository
) : BaseViewModel<BookingDetailsUiState, BookingDetailsEvent>() {

    // Store the current booking ID
    private var currentBookingId: String? = null

    /**
     * Provides initial state for the ViewModel.
     */
    override fun initialState(): BookingDetailsUiState = BookingDetailsUiState()

    /**
     * Handle events from the UI.
     */
    override fun handleEvent(event: BookingDetailsEvent) {
        when (event) {
            is BookingDetailsEvent.LoadBooking -> loadBooking(event.bookingId)
            is BookingDetailsEvent.ShowCancelDialog -> showCancelDialog()
            is BookingDetailsEvent.DismissCancelDialog -> dismissCancelDialog()
            is BookingDetailsEvent.ConfirmCancelBooking -> confirmCancelBooking()
            is BookingDetailsEvent.ShowRatingDialog -> showRatingDialog()
            is BookingDetailsEvent.DismissRatingDialog -> dismissRatingDialog()
            is BookingDetailsEvent.SubmitRating -> submitRating(event.rating, event.comment)
            is BookingDetailsEvent.ShareBooking -> shareBooking()
            is BookingDetailsEvent.ClearError -> clearError()
            is BookingDetailsEvent.NavigateBack -> { /* Handled by UI */
            }

            is BookingDetailsEvent.NavigateToProvider -> { /* Handled by UI */
            }

            is BookingDetailsEvent.NavigateToRepeatBooking -> { /* Handled by UI */
            }
        }
    }

    /**
     * Load booking details by ID.
     */
    private fun loadBooking(bookingId: String) {
        currentBookingId = bookingId

        launchWithErrorHandling(
            onError = { exception ->
                updateState {
                    copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            }
        ) {
            updateState { copy(isLoading = true, error = null) }

            // Combine booking, provider, and service flows

            bookingRepository.getBookingById(bookingId)
                .catch { exception ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load booking"
                        )
                    }
                }.collect { booking ->
                    if (booking != null) {
                        // Load provider and service separately
                        val providerFlow = providerRepository.getProviderById(booking.providerId)
                        val serviceFlow = serviceRepository.getServiceById(booking.serviceId)

                        combine(providerFlow, serviceFlow) { provider, service ->
                            BookingDetailsData(
                                booking = booking,
                                provider = provider,
                                service = service
                            )
                        }.catch { exception ->
                            // Even if provider/service loading fails, show booking
                            updateState {
                                copy(
                                    isLoading = false,
                                    booking = BookingDetailsData(
                                        booking = booking,
                                        provider = null,
                                        service = null
                                    ),
                                    error = null
                                )
                            }
                        }.collect { detailsData ->
                            updateState {
                                copy(
                                    isLoading = false,
                                    booking = detailsData,
                                    error = null
                                )
                            }
                        }
                    } else {
                        updateState {
                            copy(
                                isLoading = false,
                                error = "Booking not found"
                            )
                        }
                    }
                }
        }
    }

    /**
     * Show cancellation confirmation dialog.
     */
    private fun showCancelDialog() {
        updateState { copy(showCancelDialog = true) }
    }

    /**
     * Dismiss cancellation confirmation dialog.
     */
    private fun dismissCancelDialog() {
        updateState { copy(showCancelDialog = false) }
    }

    /**
     * Confirm and execute booking cancellation.
     */
    private fun confirmCancelBooking() {
        val bookingId = currentBookingId ?: return

        launchWithErrorHandling(
            onError = { exception ->
                updateState {
                    copy(
                        isCancelling = false,
                        error = exception.message ?: "Failed to cancel booking"
                    )
                }
            }
        ) {
            updateState { copy(isCancelling = true) }

            bookingRepository.cancelBooking(bookingId)
                .onSuccess {
                    updateState {
                        copy(
                            isCancelling = false,
                            showCancelDialog = false
                        )
                    }
                }
                .onFailure { exception ->
                    updateState {
                        copy(
                            isCancelling = false,
                            error = exception.message ?: "Failed to cancel booking"
                        )
                    }
                }
        }

        //TODO close screen or show success cancelling
    }

    /**
     * Show rating dialog (stub for now).
     */
    private fun showRatingDialog() {
        updateState { copy(showRatingDialog = true) }
    }

    /**
     * Dismiss rating dialog.
     */
    private fun dismissRatingDialog() {
        updateState { copy(showRatingDialog = false) }
    }

    /**
     * Submit rating (stub - would integrate with backend).
     */
    private fun submitRating(rating: Int, comment: String) {
        launchWithErrorHandling(
            onError = { exception ->
                updateState {
                    copy(
                        isRating = false,
                        error = exception.message ?: "Failed to submit rating"
                    )
                }
            }
        ) {
            updateState { copy(isRating = true) }

            // Simulate API call - in production would call use case
            kotlinx.coroutines.delay(1000)

            updateState {
                copy(
                    isRating = false,
                    showRatingDialog = false
                )
            }
        }
    }

    /**
     * Generate share text for booking.
     */
    private fun shareBooking() {
        val bookingData = state.value.booking ?: return
        val shareText = bookingData.generateShareText()
        updateState { copy(shareText = shareText) }
    }

    /**
     * Clear error message.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }

    /**
     * Clear share text after sharing.
     */
    fun clearShareText() {
        updateState { copy(shareText = null) }
    }
}
