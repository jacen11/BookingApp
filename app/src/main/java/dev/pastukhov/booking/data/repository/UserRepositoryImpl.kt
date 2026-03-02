package dev.pastukhov.booking.data.repository

import dev.pastukhov.booking.data.local.dao.UserDao
import dev.pastukhov.booking.data.mapper.toDomain
import dev.pastukhov.booking.data.mapper.toEntity
import dev.pastukhov.booking.data.mock.MockData
import dev.pastukhov.booking.data.remote.api.BookingApi
import dev.pastukhov.booking.domain.model.User
import dev.pastukhov.booking.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of UserRepository.
 * Uses mock data for demonstration (no real backend required).
 */
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val bookingApi: BookingApi
) : UserRepository {

    // In-memory cache for current user (simplified for demo)
    private var currentUserId: String? = null

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getUserById(currentUserId ?: "").map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            // Try local first
            val localUser = userDao.getUserByIdSync(userId)
            if (localUser != null) {
                Result.success(localUser.toDomain())
            } else {
                // Fallback to mock
                Result.success(MockData.mockUser.toDomain())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Use mock login for demo
            val mockUser = MockData.login(email, password)
            if (mockUser != null) {
                currentUserId = mockUser.id
                // Save to local database
                userDao.insertUser(mockUser.toDomain().toEntity())
                Result.success(mockUser.toDomain())
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Result<User> {
        return try {
            // Mock registration - in real app would call API
            val newUser = MockData.mockUser.copy(
                id = "user_${System.currentTimeMillis()}",
                email = email,
                name = name,
                phone = phone
            )
            currentUserId = newUser.id
            userDao.insertUser(newUser.toDomain().toEntity())
            Result.success(newUser.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            userDao.insertUser(user.toEntity())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        currentUserId?.let { userId ->
            userDao.deleteUser(userId)
        }
        currentUserId = null
    }

    override suspend fun isLoggedIn(): Boolean {
        return currentUserId != null
    }
}
