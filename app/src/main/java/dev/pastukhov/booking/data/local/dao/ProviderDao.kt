package dev.pastukhov.booking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.pastukhov.booking.data.local.entity.ProviderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Provider operations.
 */
@Dao
interface ProviderDao {

    @Query("SELECT * FROM providers")
    fun getAllProviders(): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM providers WHERE category = :category")
    fun getProvidersByCategory(category: String): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM providers WHERE id = :providerId")
    fun getProviderById(providerId: String): Flow<ProviderEntity?>

    @Query("SELECT * FROM providers WHERE id = :providerId")
    suspend fun getProviderByIdSync(providerId: String): ProviderEntity?

    @Query("SELECT * FROM providers WHERE name LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%'")
    fun searchProviders(query: String): Flow<List<ProviderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<ProviderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: ProviderEntity)

    @Query("DELETE FROM providers")
    suspend fun deleteAllProviders()
}
