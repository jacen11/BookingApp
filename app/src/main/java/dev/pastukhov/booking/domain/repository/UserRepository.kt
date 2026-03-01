package dev.pastukhov.booking.domain.repository

import dev.pastukhov.booking.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User operations.
 * Defines the contract for user data operations.
 */
interface UserRepository {

    /**
     * Get current user as a Flow.
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Get user by ID.
     */
    suspend fun getUserById(userId: String): Result<User>

    /**
     * Login with email and password.
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Register new user.
     */
    suspend fun register(email: String, password: String, name: String, phone: String): Result<User>

    /**
     * Update user profile.
     */
    suspend fun updateUser(user: User): Result<User>

    /**
     * Logout current user.
     */
    suspend fun logout()

    /**
     * Check if user is logged in.
     */
    suspend fun isLoggedIn(): Boolean
}
