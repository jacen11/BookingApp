package dev.pastukhov.booking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for local database.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val phone: String,
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Provider entity for local database.
 */
@Entity(tableName = "providers")
data class ProviderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val imageUrl: String? = null,
    val address: String,
    val city: String,
    val rating: Float,
    val reviewCount: Int,
    val phone: String,
    val workingHours: String
)

/**
 * Service entity for local database.
 */
@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val providerId: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String,
    val duration: Int,
    val imageUrl: String? = null
)

/**
 * Booking entity for local database.
 */
@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val providerId: String,
    val providerName: String,
    val serviceId: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val status: String,
    val totalPrice: Double,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
