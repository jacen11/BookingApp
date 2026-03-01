package dev.pastukhov.booking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.pastukhov.booking.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Service operations.
 */
@Dao
interface ServiceDao {

    @Query("SELECT * FROM services WHERE providerId = :providerId")
    fun getServicesByProvider(providerId: String): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :serviceId")
    fun getServiceById(serviceId: String): Flow<ServiceEntity?>

    @Query("SELECT * FROM services WHERE id = :serviceId")
    suspend fun getServiceByIdSync(serviceId: String): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Query("DELETE FROM services WHERE providerId = :providerId")
    suspend fun deleteServicesByProvider(providerId: String)

    @Query("DELETE FROM services")
    suspend fun deleteAllServices()
}
