package dev.pastukhov.booking.domain.repository

import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.TimeSlot
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for Booking operations.
 */
interface BookingRepository {

    /**
     * Get all bookings for a user.
     */
    fun getUserBookings(userId: String): Flow<List<Booking>>

    /**
     * Get booking by ID.
     */
    fun getBookingById(bookingId: String): Flow<Booking?>

    /**
     * Create a new booking.
     */
    suspend fun createBooking(booking: Booking): Result<Booking>

    /**
     * Update an existing booking.
     */
    suspend fun updateBooking(booking: Booking): Result<Booking>

    /**
     * Cancel a booking.
     */
    suspend fun cancelBooking(bookingId: String): Result<Unit>

    /**
     * Get available time slots for a provider on a specific date.
     */
    suspend fun getAvailableSlots(
        providerId: String,
        date: LocalDate,
        serviceId: String
    ): Result<List<TimeSlot>>

    /**
     * Refresh bookings from remote.
     */
    suspend fun refreshBookings(userId: String): Result<Unit>
}
