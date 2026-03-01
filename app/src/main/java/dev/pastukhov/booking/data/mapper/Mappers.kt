package dev.pastukhov.booking.data.mapper

import dev.pastukhov.booking.data.local.entity.UserEntity
import dev.pastukhov.booking.data.local.entity.ProviderEntity
import dev.pastukhov.booking.data.local.entity.ServiceEntity
import dev.pastukhov.booking.data.local.entity.BookingEntity
import dev.pastukhov.booking.data.remote.dto.UserDto
import dev.pastukhov.booking.data.remote.dto.ProviderDto
import dev.pastukhov.booking.data.remote.dto.ServiceDto
import dev.pastukhov.booking.data.remote.dto.BookingDto
import dev.pastukhov.booking.domain.model.User
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Mappers for converting between different data layers:
 * - Remote DTOs -> Domain Models
 * - Local Entities -> Domain Models
 * - Domain Models -> Local Entities
 */

// User Mappers
fun UserDto.toDomain(): User = User(
    id = id,
    email = email,
    name = name,
    phone = phone,
    avatarUrl = avatarUrl
)

fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    name = name,
    phone = phone,
    avatarUrl = avatarUrl
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    name = name,
    phone = phone,
    avatarUrl = avatarUrl
)

// Provider Mappers
fun ProviderDto.toDomain(): Provider = Provider(
    id = id,
    name = name,
    description = description,
    category = ProviderCategory.fromString(category),
    imageUrl = imageUrl,
    address = address,
    city = city,
    rating = rating,
    reviewCount = reviewCount,
    phone = phone,
    workingHours = workingHours
)

fun ProviderEntity.toDomain(): Provider = Provider(
    id = id,
    name = name,
    description = description,
    category = ProviderCategory.fromString(category),
    imageUrl = imageUrl,
    address = address,
    city = city,
    rating = rating,
    reviewCount = reviewCount,
    phone = phone,
    workingHours = workingHours
)

fun Provider.toEntity(): ProviderEntity = ProviderEntity(
    id = id,
    name = name,
    description = description,
    category = category.name,
    imageUrl = imageUrl,
    address = address,
    city = city,
    rating = rating,
    reviewCount = reviewCount,
    phone = phone,
    workingHours = workingHours
)

// Service Mappers
fun ServiceDto.toDomain(): Service = Service(
    id = id,
    providerId = providerId,
    name = name,
    description = description,
    price = price,
    currency = currency,
    duration = duration,
    imageUrl = imageUrl
)

fun ServiceEntity.toDomain(): Service = Service(
    id = id,
    providerId = providerId,
    name = name,
    description = description,
    price = price,
    currency = currency,
    duration = duration,
    imageUrl = imageUrl
)

fun Service.toEntity(): ServiceEntity = ServiceEntity(
    id = id,
    providerId = providerId,
    name = name,
    description = description,
    price = price,
    currency = currency,
    duration = duration,
    imageUrl = imageUrl
)

// Booking Mappers
fun BookingDto.toDomain(): Booking = Booking(
    id = id,
    userId = userId,
    providerId = providerId,
    providerName = providerName,
    serviceId = serviceId,
    serviceName = serviceName,
    date = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE),
    time = LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME),
    status = BookingStatus.fromString(status),
    totalPrice = totalPrice,
    notes = notes
)

fun BookingEntity.toDomain(): Booking = Booking(
    id = id,
    userId = userId,
    providerId = providerId,
    providerName = providerName,
    serviceId = serviceId,
    serviceName = serviceName,
    date = LocalDate.parse(date),
    time = LocalTime.parse(time),
    status = BookingStatus.fromString(status),
    totalPrice = totalPrice,
    notes = notes
)

fun Booking.toEntity(): BookingEntity = BookingEntity(
    id = id,
    userId = userId,
    providerId = providerId,
    providerName = providerName,
    serviceId = serviceId,
    serviceName = serviceName,
    date = date.toString(),
    time = time.toString(),
    status = status.name,
    totalPrice = totalPrice,
    notes = notes
)

fun Booking.toDto(): BookingDto = BookingDto(
    id = id,
    userId = userId,
    providerId = providerId,
    providerName = providerName,
    serviceId = serviceId,
    serviceName = serviceName,
    date = date.toString(),
    time = time.toString(),
    status = status.name,
    totalPrice = totalPrice,
    notes = notes
)
