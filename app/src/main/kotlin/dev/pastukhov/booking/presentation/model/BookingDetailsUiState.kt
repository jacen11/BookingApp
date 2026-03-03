package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service

/**
 * UI State for Booking Details screen.
 */
data class BookingDetailsUiState(
    val isLoading: Boolean = true,
    val booking: BookingDetailsData? = null,
    val error: String? = null,
    val showCancelDialog: Boolean = false,
    val showRatingDialog: Boolean = false,
    val isCancelling: Boolean = false,
    val isRating: Boolean = false,
    val shareText: String? = null
)

/**
 * Combined data for booking details display.
 */
data class BookingDetailsData(
    val booking: Booking,
    val provider: Provider? = null,
    val service: Service? = null
) {
    /**
     * Check if booking is active (Pending or Confirmed).
     */
    val isActive: Boolean
        get() = booking.status == BookingStatus.PENDING || booking.status == BookingStatus.CONFIRMED

    /**
     * Check if booking is completed.
     */
    val isCompleted: Boolean
        get() = booking.status == BookingStatus.COMPLETED

    /**
     * Check if booking is cancelled.
     */
    val isCancelled: Boolean
        get() = booking.status == BookingStatus.CANCELLED || booking.status == BookingStatus.NO_SHOW

    /**
     * Get status display text based on current locale.
     */
    fun getStatusDisplayText(): String = when (booking.status) {
        BookingStatus.PENDING -> "Pendiente"
        BookingStatus.CONFIRMED -> "Confirmado"
        BookingStatus.COMPLETED -> "Completado"
        BookingStatus.CANCELLED -> "Cancelado"
        BookingStatus.NO_SHOW -> "No asistido"
    }

    /**
     * Format date and time for display with locale support.
     */
    fun formattedDateTime(locale: String = "es"): String {
        val day = booking.date.dayOfMonth
        val month = booking.date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val year = booking.date.year
        val time = String.format("%02d:%02d", booking.time.hour, booking.time.minute)

        return if (locale == "en") {
            // English format: January 25, 2025 at 14:30
            val monthEn = when (booking.date.month.name) {
                "JANUARY" -> "January"
                "FEBRUARY" -> "February"
                "MARCH" -> "March"
                "APRIL" -> "April"
                "MAY" -> "May"
                "JUNE" -> "June"
                "JULY" -> "July"
                "AUGUST" -> "August"
                "SEPTEMBER" -> "September"
                "OCTOBER" -> "October"
                "NOVEMBER" -> "November"
                "DECEMBER" -> "December"
                else -> month
            }
            "$monthEn $day, $year at $time"
        } else {
            // Spanish format: 25 de enero de 2025, 14:30
            "$day de $month de $year, $time"
        }
    }

    /**
     * Generate share text for the booking.
     */
    fun generateShareText(): String {
        return buildString {
            appendLine("📅 ${service?.name ?: "Service"}")
            appendLine("🏠 ${booking.providerName}")
            appendLine("📍 ${booking.providerAddress}")
            appendLine("🕐 ${formattedDateTime()}")
            appendLine("💰 $${booking.totalPrice}")
            appendLine("📋 #${booking.id}")
        }
    }
}

/**
 * Events for Booking Details screen.
 */
sealed class BookingDetailsEvent {
    data class LoadBooking(val bookingId: String) : BookingDetailsEvent()
    data object ShowCancelDialog : BookingDetailsEvent()
    data object DismissCancelDialog : BookingDetailsEvent()
    data object ConfirmCancelBooking : BookingDetailsEvent()
    data object ShowRatingDialog : BookingDetailsEvent()
    data object DismissRatingDialog : BookingDetailsEvent()
    data class SubmitRating(val rating: Int, val comment: String) : BookingDetailsEvent()
    data object ShareBooking : BookingDetailsEvent()
    data object ClearError : BookingDetailsEvent()
    data object NavigateBack : BookingDetailsEvent()
    data class NavigateToProvider(val providerId: String) : BookingDetailsEvent()
    data class NavigateToRepeatBooking(val providerId: String, val serviceId: String) : BookingDetailsEvent()
}
