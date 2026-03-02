package dev.pastukhov.booking.domain.model

/**
 * Domain model for User.
 */
data class User(
    val id: String,
    val email: String,
    val name: String,
    val phone: String,
    val avatarUrl: String? = null
)

/**
 * Domain model for authentication state.
 */
sealed class AuthState {
    data object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
