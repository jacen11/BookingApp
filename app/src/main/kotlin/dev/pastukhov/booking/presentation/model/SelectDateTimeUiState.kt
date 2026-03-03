package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.model.TimeSlot
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI State for Select Date & Time Screen.
 */
data class SelectDateTimeUiState(
    val provider: Provider? = null,
    val service: Service? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val availableTimeSlots: List<TimeSlot> = emptyList(),
    val isLoadingSlots: Boolean = false,
    val dateError: String? = null,
    val timeError: String? = null,
    val error: String? = null
) {
    val isDateTimeSelected: Boolean
        get() = selectedDate != null && selectedTime != null

    val canProceed: Boolean
        get() = isDateTimeSelected && dateError == null && timeError == null

    fun getMinDate(): LocalDate = LocalDate.now().plusDays(1)

    fun getMaxDate(): LocalDate = LocalDate.now().plusDays(30)
}