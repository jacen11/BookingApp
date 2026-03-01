package dev.pastukhov.booking.data.remote.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Booking DTO for appointment data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BookingDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("userId") val userId: String,
    @JsonProperty("providerId") val providerId: String,
    @JsonProperty("providerName") val providerName: String,
    @JsonProperty("serviceId") val serviceId: String,
    @JsonProperty("serviceName") val serviceName: String,
    @JsonProperty("date") val date: String,
    @JsonProperty("time") val time: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("totalPrice") val totalPrice: Double,
    @JsonProperty("notes") val notes: String? = null,
    @JsonProperty("createdAt") val createdAt: String? = null
)
