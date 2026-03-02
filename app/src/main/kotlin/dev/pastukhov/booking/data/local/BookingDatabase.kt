package dev.pastukhov.booking.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.pastukhov.booking.data.local.dao.BookingDao
import dev.pastukhov.booking.data.local.dao.ProviderDao
import dev.pastukhov.booking.data.local.dao.ServiceDao
import dev.pastukhov.booking.data.local.dao.UserDao
import dev.pastukhov.booking.data.local.entity.BookingEntity
import dev.pastukhov.booking.data.local.entity.ProviderEntity
import dev.pastukhov.booking.data.local.entity.ServiceEntity
import dev.pastukhov.booking.data.local.entity.UserEntity

/**
 * Room database for Booking App.
 * Contains tables for Users, Providers, Services, and Bookings.
 */
@Database(
    entities = [
        UserEntity::class,
        ProviderEntity::class,
        ServiceEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class BookingDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun providerDao(): ProviderDao
    abstract fun serviceDao(): ServiceDao
    abstract fun bookingDao(): BookingDao

    companion object {
        const val DATABASE_NAME = "booking_database"
    }
}
