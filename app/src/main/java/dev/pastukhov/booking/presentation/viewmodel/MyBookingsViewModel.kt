package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import dev.pastukhov.booking.domain.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for My Bookings screen.
 */
data class MyBookingsUiState(
    val isLoading: Boolean = true,
    val activeBookings: List<Booking> = emptyList(),
    val completedBookings: List<Booking> = emptyList(),
    val cancelledBookings: List<Booking> = emptyList(),
    val selectedTab: BookingTab = BookingTab.ACTIVE,
    val error: String? = null,
    val showCancelDialog: Boolean = false,
    val bookingToCancel: Booking? = null,
    val isCancelling: Boolean = false
) {
    /**
     * Returns bookings for the currently selected tab.
     */
    val currentBookings: List<Booking>
        get() = when (selectedTab) {
            BookingTab.ACTIVE -> activeBookings
            BookingTab.HISTORY -> completedBookings
            BookingTab.CANCELLED -> cancelledBookings
        }

    /**
     * Check if the current tab has no bookings.
     */
    val isEmpty: Boolean
        get() = currentBookings.isEmpty() && !isLoading && error == null
}

/**
 * Tab selection for My Bookings.
 */
enum class BookingTab {
    ACTIVE,    // Pending + Confirmed
    HISTORY,   // Completed
    CANCELLED  // Cancelled + NoShow
}

/**
 * Extension to filter bookings by tab.
 */
fun List<Booking>.filterByTab(tab: BookingTab): List<Booking> {
    return when (tab) {
        BookingTab.ACTIVE -> filter { it.status == BookingStatus.PENDING || it.status == BookingStatus.CONFIRMED }
        BookingTab.HISTORY -> filter { it.status == BookingStatus.COMPLETED }
        BookingTab.CANCELLED -> filter { it.status == BookingStatus.CANCELLED || it.status == BookingStatus.NO_SHOW }
    }
}

/**
 * ViewModel for My Bookings screen.
 * Handles loading, filtering, and actions on bookings.
 */
@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : androidx.lifecycle.ViewModel() {

    private val _uiState = MutableStateFlow(MyBookingsUiState())
    val uiState: StateFlow<MyBookingsUiState> = _uiState.asStateFlow()

    // Default user ID for demo - in production would come from auth
    private val currentUserId = "user_001"

    init {
        loadBookings()
    }

    /**
     * Load all bookings from repository and categorize them.
     */
    fun loadBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            bookingRepository.getUserBookings(currentUserId)
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                }
                .collect { bookings ->
                    val active = bookings.filterByTab(BookingTab.ACTIVE)
                    val completed = bookings.filterByTab(BookingTab.HISTORY)
                    val cancelled = bookings.filterByTab(BookingTab.CANCELLED)

                    _uiState.update {
                        it.copy(
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
    fun selectTab(tab: BookingTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    /**
     * Show confirmation dialog before cancelling a booking.
     */
    fun showCancelDialog(booking: Booking) {
        _uiState.update {
            it.copy(showCancelDialog = true, bookingToCancel = booking)
        }
    }

    /**
     * Dismiss cancel confirmation dialog.
     */
    fun dismissCancelDialog() {
        _uiState.update {
            it.copy(showCancelDialog = false, bookingToCancel = null)
        }
    }

    /**
     * Confirm and execute booking cancellation.
     */
    fun confirmCancelBooking() {
        val booking = _uiState.value.bookingToCancel ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true) }

            bookingRepository.cancelBooking(booking.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isCancelling = false,
                            showCancelDialog = false,
                            bookingToCancel = null
                        )
                    }
                    // Reload to update all lists
                    loadBookings()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
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
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
