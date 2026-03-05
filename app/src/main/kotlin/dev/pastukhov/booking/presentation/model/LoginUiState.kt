package dev.pastukhov.booking.presentation.model

/**
 * UI state for Login screen.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)
