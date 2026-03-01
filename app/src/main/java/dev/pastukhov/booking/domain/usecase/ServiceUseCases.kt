package dev.pastukhov.booking.domain.usecase

import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting services by provider.
 */
class GetServicesUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    /**
     * Get all services for a provider.
     */
    operator fun invoke(providerId: String): Flow<List<Service>> {
        return serviceRepository.getServicesByProvider(providerId)
    }

    /**
     * Get single service by ID.
     */
    fun byId(serviceId: String): Flow<Service?> {
        return serviceRepository.getServiceById(serviceId)
    }
}

/**
 * Use case for refreshing services from remote.
 */
class RefreshServicesUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(providerId: String): Result<Unit> {
        return serviceRepository.refreshServices(providerId)
    }
}
