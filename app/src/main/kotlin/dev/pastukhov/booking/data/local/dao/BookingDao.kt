package dev.pastukhov.booking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.pastukhov.booking.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Booking operations.
 */
@Dao
interface BookingDao {

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY createdAt DESC")
    fun getBookingsByUser(userId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    fun getBookingById(bookingId: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingByIdSync(bookingId: String): BookingEntity?

    @Query("SELECT * FROM bookings WHERE status = :status")
    fun getBookingsByStatus(status: String): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Query("UPDATE bookings SET status = :status WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, status: String)

    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteBooking(bookingId: String)

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
}
