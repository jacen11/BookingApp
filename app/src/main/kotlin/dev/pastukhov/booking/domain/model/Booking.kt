package dev.pastukhov.booking.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model for Booking appointment.
 */
data class Booking(
    val id: String,
    val userId: String,
    val providerId: String,
    val providerName: String,
    val providerAddress: String,
    val serviceId: String,
    val serviceName: String,
    val date: LocalDate,
    val time: LocalTime,
    val status: BookingStatus,
    val totalPrice: Double,
    val notes: String? = null,
    // Payment fields
    val paymentMethod: PaymentMethod? = null,
    val cardNumber: String? = null,
    val cardExpiry: String? = null,
    val isPaid: Boolean = false
) {
    /**
     * Returns formatted date string.
     */
    fun formattedDate(): String {
        val day = date.dayOfMonth
        val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val year = date.year
        return "$day de $month de $year"
    }

    /**
     * Returns formatted time string.
     */
    fun formattedTime(): String {
        val hour = time.hour
        val minute = time.minute
        return String.format("%02d:%02d", hour, minute)
    }

    /**
     * Returns formatted price string.
     */
    fun formattedPrice(): String = "$totalPrice"

    /**
     * Returns masked card number (last 4 digits).
     */
    fun maskedCardNumber(): String? {
        return cardNumber?.let { "**** **** **** ${it.takeLast(4)}" }
    }
}

/**
 * Status of a booking.
 */
enum class BookingStatus(val displayName: String) {
    PENDING("Pendiente"),
    CONFIRMED("Confirmado"),
    COMPLETED("Completado"),
    CANCELLED("Cancelado"),
    NO_SHOW("No asistido");

    companion object {
        fun fromString(value: String): BookingStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}

/**
 * Payment method options.
 */
enum class PaymentMethod(val displayName: String) {
    CARD("Tarjeta"),
    CASH("Efectivo"),
    GOOGLE_PAY("Google Pay");

    companion object {
        fun fromString(value: String): PaymentMethod {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: CARD
        }
    }
}

/**
 * Available time slot for booking.
 */
data class TimeSlot(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isAvailable: Boolean
)
