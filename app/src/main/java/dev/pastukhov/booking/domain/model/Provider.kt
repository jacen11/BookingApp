package dev.pastukhov.booking.domain.model

/**
 * Domain model for Service Provider (salon, clinic, master).
 */
data class Provider(
    val id: String,
    val name: String,
    val description: String,
    val category: ProviderCategory,
    val imageUrl: String? = null,
    val address: String,
    val city: String,
    val rating: Float,
    val reviewCount: Int,
    val phone: String,
    val workingHours: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val priceRange: String = "$$"
)

/**
 * Categories for service providers.
 */
enum class ProviderCategory(val displayName: String) {
    SALON("Salón"),
    CLINIC("Clínica"),
    MASTER("Maestro"),
    SPA("Spa"),
    BARBER("Barbería"),
    BEAUTY("Belleza"),
    FITNESS("Fitness"),
    OTHER("Otro");

    companion object {
        fun fromString(value: String): ProviderCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}
