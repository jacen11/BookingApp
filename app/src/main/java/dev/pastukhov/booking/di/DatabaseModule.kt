package dev.pastukhov.booking.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pastukhov.booking.data.local.BookingDatabase
import dev.pastukhov.booking.data.local.BookingDatabase.Companion.DATABASE_NAME
import dev.pastukhov.booking.data.local.dao.BookingDao
import dev.pastukhov.booking.data.local.dao.ProviderDao
import dev.pastukhov.booking.data.local.dao.ServiceDao
import dev.pastukhov.booking.data.local.dao.UserDao
import javax.inject.Singleton

/**
 * Hilt module for database dependencies (Room).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BookingDatabase {
        return Room.databaseBuilder(
            context,
            BookingDatabase::class.java,
            DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: BookingDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideServiceDao(database: BookingDatabase): ServiceDao {
        return database.serviceDao()
    }

    @Provides
    @Singleton
    fun provideProviderDao(database: BookingDatabase): ProviderDao {
        return database.providerDao()
    }

    @Provides
    @Singleton
    fun provideBookingDao(database: BookingDatabase): BookingDao {
        return database.bookingDao()
    }
}
