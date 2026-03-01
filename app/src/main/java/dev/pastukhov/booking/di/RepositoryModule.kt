package dev.pastukhov.booking.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pastukhov.booking.data.repository.BookingRepositoryImpl
import dev.pastukhov.booking.data.repository.ProviderRepositoryImpl
import dev.pastukhov.booking.data.repository.ServiceRepositoryImpl
import dev.pastukhov.booking.data.repository.UserRepositoryImpl
import dev.pastukhov.booking.domain.repository.BookingRepository
import dev.pastukhov.booking.domain.repository.ProviderRepository
import dev.pastukhov.booking.domain.repository.ServiceRepository
import dev.pastukhov.booking.domain.repository.UserRepository
import javax.inject.Singleton

/**
 * Hilt module for repository bindings.
 * Binds repository interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindProviderRepository(
        providerRepositoryImpl: ProviderRepositoryImpl
    ): ProviderRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(
        serviceRepositoryImpl: ServiceRepositoryImpl
    ): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository
}
