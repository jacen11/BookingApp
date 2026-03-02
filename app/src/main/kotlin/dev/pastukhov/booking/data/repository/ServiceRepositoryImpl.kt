package dev.pastukhov.booking.data.repository

import dev.pastukhov.booking.data.local.dao.ServiceDao
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mapper.toEntity
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.data.remote.api.BookingApi
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ServiceRepository.
 * Uses mock data for demonstration (no real backend required).
 */
class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao,
    private val bookingApi: BookingApi
) : ServiceRepository {

    override fun getServicesByProvider(providerId: String): Flow<List<Service>> {
        return serviceDao.getServicesByProvider(providerId).map { entities ->
            if (entities.isEmpty()) {
                // Return mock data if local is empty
                MockData.getServicesForProvider(providerId).map { it.toDomain() }
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getServiceById(serviceId: String): Flow<Service?> {
        return serviceDao.getServiceById(serviceId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun refreshServices(providerId: String): Result<Unit> {
        return try {
            // In real app would call API
            // For demo, we use mock data
            val services = MockData.getServicesForProvider(providerId)
                .map { it.toDomain().toEntity() }
            serviceDao.deleteServicesByProvider(providerId)
            serviceDao.insertServices(services)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
