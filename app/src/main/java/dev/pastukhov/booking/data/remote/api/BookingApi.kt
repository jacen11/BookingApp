package dev.pastukhov.booking.data.remote.api

import dev.pastukhov.booking.data.remote.dto.BookingDto
import dev.pastukhov.booking.data.remote.dto.ProviderDto
import dev.pastukhov.booking.data.remote.dto.ServiceDto
import dev.pastukhov.booking.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for Booking App.
 * All endpoints are designed for RESTful communication.
 */
interface BookingApi {

    // User endpoints
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): Response<UserDto>

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<UserDto>

    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserDto>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body user: UserDto
    ): Response<UserDto>

    // Provider endpoints
    @GET("providers")
    suspend fun getProviders(
        @Query("category") category: String? = null,
        @Query("location") location: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<ProviderDto>>

    @GET("providers/{id}")
    suspend fun getProvider(@Path("id") providerId: String): Response<ProviderDto>

    // Service endpoints
    @GET("providers/{providerId}/services")
    suspend fun getServices(
        @Path("providerId") providerId: String
    ): Response<List<ServiceDto>>

    @GET("services/{id}")
    suspend fun getService(@Path("id") serviceId: String): Response<ServiceDto>

    // Booking endpoints
    @GET("bookings")
    suspend fun getUserBookings(
        @Query("userId") userId: String
    ): Response<List<BookingDto>>

    @GET("bookings/{id}")
    suspend fun getBooking(@Path("id") bookingId: String): Response<BookingDto>

    @POST("bookings")
    suspend fun createBooking(@Body booking: BookingDto): Response<BookingDto>

    @PUT("bookings/{id}")
    suspend fun updateBooking(
        @Path("id") bookingId: String,
        @Body booking: BookingDto
    ): Response<BookingDto>

    @DELETE("bookings/{id}")
    suspend fun cancelBooking(@Path("id") bookingId: String): Response<Unit>

    // Availability
    @GET("providers/{providerId}/availability")
    suspend fun getAvailableSlots(
        @Path("providerId") providerId: String,
        @Query("date") date: String,
        @Query("serviceId") serviceId: String
    ): Response<List<AvailableSlot>>
}

/**
 * Request body for login.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Request body for registration.
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String
)

/**
 * Available time slot for booking.
 */
data class AvailableSlot(
    val startTime: String,
    val endTime: String,
    val available: Boolean
)
