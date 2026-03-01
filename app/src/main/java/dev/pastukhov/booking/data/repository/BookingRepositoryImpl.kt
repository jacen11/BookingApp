package dev.pastukhov.booking.data.repository

import dev.pastukhov.booking.data.local.dao.BookingDao
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mapper.toDto
import dev.pastukhov.booking.data.mapper.toEntity
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.data.remote.api.BookingApi
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import dev.pastukhov.booking.domain.model.TimeSlot
import dev.pastukhov.booking.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * Implementation of BookingRepository.
 * Uses mock data for demonstration (no real backend required).
 */
class BookingRepositoryImpl @Inject constructor(
    private val bookingDao: BookingDao,
    private val bookingApi: BookingApi
) : BookingRepository {

    // In-memory storage for bookings (demo only)
    private val bookings = mutableListOf<Booking>()

    init {
        // Initialize with mock bookings
        bookings.addAll(MockData.mockBookings.map { it.toDomain() })
    }

    override fun getUserBookings(userId: String): Flow<List<Booking>> {
        return bookingDao.getBookingsByUser(userId).map { entities ->
            if (entities.isEmpty()) {
                // Return mock data filtered by user
                bookings.filter { it.userId == userId }
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getBookingById(bookingId: String): Flow<Booking?> {
        return bookingDao.getBookingById(bookingId).map { entity ->
            entity?.toDomain() ?: bookings.find { it.id == bookingId }
        }
    }

    override suspend fun createBooking(booking: Booking): Result<Booking> {
        return try {
            // In real app would call API
            val newBooking = booking.copy(
                id = "booking_${System.currentTimeMillis()}",
                status = BookingStatus.PENDING
            )
            bookings.add(newBooking)
            bookingDao.insertBooking(newBooking.toEntity())
            Result.success(newBooking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBooking(booking: Booking): Result<Booking> {
        return try {
            val index = bookings.indexOfFirst { it.id == booking.id }
            if (index >= 0) {
                bookings[index] = booking
            }
            bookingDao.insertBooking(booking.toEntity())
            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelBooking(bookingId: String): Result<Unit> {
        return try {
            val index = bookings.indexOfFirst { it.id == bookingId }
            if (index >= 0) {
                bookings[index] = bookings[index].copy(status = BookingStatus.CANCELLED)
                bookingDao.updateBookingStatus(bookingId, BookingStatus.CANCELLED.name)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableSlots(
        providerId: String,
        date: LocalDate,
        serviceId: String
    ): Result<List<TimeSlot>> {
        return try {
            // Generate mock available slots
            val slots = generateTimeSlots()
            Result.success(slots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshBookings(userId: String): Result<Unit> {
        return try {
            // In real app would call API
            // For demo, we just sync mock bookings to local
            val bookingsToInsert = bookings
                .filter { it.userId == userId }
                .map { it.toEntity() }
            bookingDao.insertBookings(bookingsToInsert)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate mock time slots for a day.
     * Simulates available appointments from 9:00 to 18:00.
     */
    private fun generateTimeSlots(): List<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()
        var currentTime = LocalTime.of(9, 0)

        while (currentTime.hour < 18) {
            val endTime = currentTime.plusMinutes(30)
            // Randomly mark some slots as unavailable for demo
            val isAvailable = (0..10).random() > 2

            slots.add(
                TimeSlot(
                    startTime = currentTime,
                    endTime = endTime,
                    isAvailable = isAvailable
                )
            )
            currentTime = endTime
        }

        return slots
    }
}
