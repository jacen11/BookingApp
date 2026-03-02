package dev.pastukhov.booking.data.remote.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Service DTO offered by providers.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ServiceDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("providerId") val providerId: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("price") val price: Double,
    @JsonProperty("currency") val currency: String = "USD",
    @JsonProperty("duration") val duration: Int, // in minutes
    @JsonProperty("imageUrl") val imageUrl: String? = null
)
