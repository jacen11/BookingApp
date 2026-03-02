package dev.pastukhov.booking.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.repository.BookingRepository
import dev.pastukhov.booking.presentation.model.BookingTab
import dev.pastukhov.booking.presentation.model.MyBookingsEvent
import dev.pastukhov.booking.presentation.model.MyBookingsUiState
import dev.pastukhov.booking.presentation.model.filterByTab
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

/**
 * ViewModel for My Bookings screen.
 * Handles loading, filtering, and actions on bookings.
 */
@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : BaseViewModel<MyBookingsUiState, MyBookingsEvent>() {

    // Default user ID for demo - in production would come from auth
    private val currentUserId = "user_001"

    init {
        handleEvent(MyBookingsEvent.LoadBookings)
    }

    /**
     * Handle events from the UI.
     */
    override fun handleEvent(event: MyBookingsEvent) {
        when (event) {
            is MyBookingsEvent.LoadBookings -> loadBookings()
            is MyBookingsEvent.SelectTab -> selectTab(event.tab)
            is MyBookingsEvent.ShowCancelDialog -> showCancelDialog(event.booking)
            is MyBookingsEvent.DismissCancelDialog -> dismissCancelDialog()
            is MyBookingsEvent.ConfirmCancelBooking -> confirmCancelBooking()
            is MyBookingsEvent.ClearError -> clearError()
        }
    }

    /**
     * Provides initial state for the ViewModel.
     */
    override fun initialState(): MyBookingsUiState = MyBookingsUiState()

    /**
     * Load all bookings from repository and categorize them.
     */
    private fun loadBookings() {
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

            bookingRepository.getUserBookings(currentUserId)
                .catch { exception ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                }
                .collect { bookings ->
                    val active = bookings.filterByTab(BookingTab.ACTIVE)
                    val completed = bookings.filterByTab(BookingTab.HISTORY)
                    val cancelled = bookings.filterByTab(BookingTab.CANCELLED)

                    updateState {
                        copy(
                            isLoading = false,
                            activeBookings = active,
                            completedBookings = completed,
                            cancelledBookings = cancelled,
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * Switch between tabs.
     */
    private fun selectTab(tab: BookingTab) {
        updateState { copy(selectedTab = tab) }
    }

    /**
     * Show confirmation dialog before cancelling a booking.
     */
    private fun showCancelDialog(booking: Booking) {
        updateState {
            copy(showCancelDialog = true, bookingToCancel = booking)
        }
    }

    /**
     * Dismiss cancel confirmation dialog.
     */
    private fun dismissCancelDialog() {
        updateState {
            copy(showCancelDialog = false, bookingToCancel = null)
        }
    }

    /**
     * Confirm and execute booking cancellation.
     */
    private fun confirmCancelBooking() {
        val booking = state.value.bookingToCancel ?: return

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

            bookingRepository.cancelBooking(booking.id)
                .onSuccess {
                    updateState {
                        copy(
                            isCancelling = false,
                            showCancelDialog = false,
                            bookingToCancel = null
                        )
                    }
                    // Reload to update all lists
                    handleEvent(MyBookingsEvent.LoadBookings)
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
    }

    /**
     * Clear error message.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }
}
