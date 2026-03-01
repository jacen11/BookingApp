package dev.pastukhov.booking.domain.usecase

import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.repository.ProviderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting providers.
 */
class GetProvidersUseCase @Inject constructor(
    private val providerRepository: ProviderRepository
) {
    /**
     * Get all providers.
     */
    operator fun invoke(): Flow<List<Provider>> {
        return providerRepository.getProviders()
    }

    /**
     * Get providers filtered by category.
     */
    fun byCategory(category: ProviderCategory): Flow<List<Provider>> {
        return providerRepository.getProvidersByCategory(category)
    }

    /**
     * Search providers by query.
     */
    fun search(query: String): Flow<List<Provider>> {
        return providerRepository.searchProviders(query)
    }

    /**
     * Get single provider by ID.
     */
    fun byId(providerId: String): Flow<Provider?> {
        return providerRepository.getProviderById(providerId)
    }
}

/**
 * Use case for refreshing providers from remote.
 */
class RefreshProvidersUseCase @Inject constructor(
    private val providerRepository: ProviderRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return providerRepository.refreshProviders()
    }
}
