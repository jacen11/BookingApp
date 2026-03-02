package dev.pastukhov.booking.data.repository

import dev.pastukhov.booking.data.local.dao.ProviderDao
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mapper.toEntity
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.data.remote.api.BookingApi
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.repository.ProviderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ProviderRepository.
 * Uses mock data for demonstration (no real backend required).
 */
class ProviderRepositoryImpl @Inject constructor(
    private val providerDao: ProviderDao,
    private val bookingApi: BookingApi
) : ProviderRepository {

    override fun getProviders(): Flow<List<Provider>> {
        return providerDao.getAllProviders().map { entities ->
            if (entities.isEmpty()) {
                // Return mock data if local is empty
                MockData.mockProviders.map { it.toDomain() }
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getProvidersByCategory(category: ProviderCategory): Flow<List<Provider>> {
        return providerDao.getProvidersByCategory(category.name).map { entities ->
            if (entities.isEmpty()) {
                // Return filtered mock data if local is empty
                MockData.mockProviders
                    .filter { it.category.equals(category.name, ignoreCase = true) }
                    .map { it.toDomain() }
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getProviderById(providerId: String): Flow<Provider?> {
        return providerDao.getProviderById(providerId).map { entity ->
            entity?.toDomain() ?: MockData.mockProviders
                .find { it.id == providerId }
                ?.toDomain()
        }
    }

    override fun searchProviders(query: String): Flow<List<Provider>> {
        return providerDao.searchProviders(query).map { entities ->
            if (entities.isEmpty()) {
                // Return filtered mock data if local is empty
                MockData.mockProviders
                    .filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.city.contains(query, ignoreCase = true)
                    }
                    .map { it.toDomain() }
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun refreshProviders(): Result<Unit> {
        return try {
            // In real app would call API
            // For demo, we use mock data
            val providers = MockData.mockProviders.map { it.toDomain().toEntity() }
            providerDao.insertProviders(providers)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
