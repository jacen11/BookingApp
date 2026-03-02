package dev.pastukhov.booking.domain.repository

import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Provider operations.
 */
interface ProviderRepository {

    /**
     * Get all providers.
     */
    fun getProviders(): Flow<List<Provider>>

    /**
     * Get providers by category.
     */
    fun getProvidersByCategory(category: ProviderCategory): Flow<List<Provider>>

    /**
     * Get provider by ID.
     */
    fun getProviderById(providerId: String): Flow<Provider?>

    /**
     * Search providers by name or city.
     */
    fun searchProviders(query: String): Flow<List<Provider>>

    /**
     * Refresh providers from remote.
     */
    suspend fun refreshProviders(): Result<Unit>
}
