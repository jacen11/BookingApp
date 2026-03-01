package dev.pastukhov.booking.domain.usecase

import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.TimeSlot
import dev.pastukhov.booking.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for getting user bookings.
 */
class GetUserBookingsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Get all bookings for a user.
     */
    operator fun invoke(userId: String): Flow<List<Booking>> {
        return bookingRepository.getUserBookings(userId)
    }

    /**
     * Get single booking by ID.
     */
    fun byId(bookingId: String): Flow<Booking?> {
        return bookingRepository.getBookingById(bookingId)
    }
}

/**
 * Use case for creating a new booking.
 */
class CreateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(booking: Booking): Result<Booking> {
        return bookingRepository.createBooking(booking)
    }
}

/**
 * Use case for cancelling a booking.
 */
class CancelBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(bookingId: String): Result<Unit> {
        return bookingRepository.cancelBooking(bookingId)
    }
}

/**
 * Use case for getting available time slots.
 */
class GetAvailableSlotsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(
        providerId: String,
        date: LocalDate,
        serviceId: String
    ): Result<List<TimeSlot>> {
        return bookingRepository.getAvailableSlots(providerId, date, serviceId)
    }
}

/**
 * Use case for refreshing bookings from remote.
 */
class RefreshBookingsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return bookingRepository.refreshBookings(userId)
    }
}
