package dev.pastukhov.booking.domain.repository

import dev.pastukhov.booking.domain.model.Service
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Service operations.
 */
interface ServiceRepository {

    /**
     * Get services by provider ID.
     */
    fun getServicesByProvider(providerId: String): Flow<List<Service>>

    /**
     * Get service by ID.
     */
    fun getServiceById(serviceId: String): Flow<Service?>

    /**
     * Refresh services from remote for a provider.
     */
    suspend fun refreshServices(providerId: String): Result<Unit>
}
