package dev.pastukhov.booking.data.remote.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * User DTO for API communication.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class UserDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("phone") val phone: String,
    @JsonProperty("avatarUrl") val avatarUrl: String? = null,
    @JsonProperty("createdAt") val createdAt: String? = null
)
