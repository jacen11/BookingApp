package dev.pastukhov.booking.domain.model

/**
 * Domain model for Service offered by providers.
 */
data class Service(
    val id: String,
    val providerId: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val duration: Int, // Duration in minutes
    val imageUrl: String? = null
) {
    /**
     * Returns formatted price string.
     */
    fun formattedPrice(): String = "$$price $currency"

    /**
     * Returns formatted duration string.
     */
    fun formattedDuration(): String {
        return when {
            duration >= 60 -> {
                val hours = duration / 60
                val minutes = duration % 60
                if (minutes > 0) "${hours}h ${minutes}min" else "${hours}h"
            }
            else -> "${duration}min"
        }
    }
}
