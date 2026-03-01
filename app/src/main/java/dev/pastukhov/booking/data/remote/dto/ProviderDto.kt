package dev.pastukhov.booking.data.remote.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Provider/Service business DTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProviderDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("category") val category: String,
    @JsonProperty("imageUrl") val imageUrl: String? = null,
    @JsonProperty("address") val address: String,
    @JsonProperty("city") val city: String,
    @JsonProperty("rating") val rating: Float,
    @JsonProperty("reviewCount") val reviewCount: Int,
    @JsonProperty("phone") val phone: String,
    @JsonProperty("workingHours") val workingHours: String
)
