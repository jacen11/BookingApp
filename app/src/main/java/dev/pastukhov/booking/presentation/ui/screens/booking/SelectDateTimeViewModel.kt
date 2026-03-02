package dev.pastukhov.booking.presentation.ui.screens.booking

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.domain.model.TimeSlot
import dev.pastukhov.booking.presentation.viewmodel.BaseViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel for Select Date & Time Screen.
 * Manages date and time selection for booking.
 */
@HiltViewModel
class SelectDateTimeViewModel @Inject constructor() : BaseViewModel<SelectDateTimeUiState, Any>() {

    override fun initialState(): SelectDateTimeUiState = SelectDateTimeUiState()

    /**
     * Initialize booking with provider and service data.
     */
    fun initializeBooking(providerId: String, serviceId: String) {
        val provider = MockData.mockProviders.find { it.id == providerId }
        val service = MockData.getServicesForProvider(providerId).find { it.id == serviceId }

        if (provider != null && service != null) {
            updateState {
                copy(
                    provider = provider.toDomain(),
                    service = service.toDomain()
                )
            }
            loadTimeSlots()
        }
    }

    /**
     * Generate mock available time slots for a given date.
     */
    fun loadTimeSlots(date: LocalDate = LocalDate.now()) {
        launchWithErrorHandling(
            onError = { throwable ->
                updateState { copy(error = throwable.message, isLoadingSlots = false) }
            }
        ) {
            updateState { copy(isLoadingSlots = true) }

            kotlinx.coroutines.delay(500)

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

        for (hour in startHour until endHour) {
            for (minute in listOf(0, 30)) {
                val startTime = LocalTime.of(hour, minute)
                val endTime = startTime.plusMinutes(30)

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
     * Clear error message.
     */
    fun clearError() {
        updateState { copy(error = null) }
    }
}
