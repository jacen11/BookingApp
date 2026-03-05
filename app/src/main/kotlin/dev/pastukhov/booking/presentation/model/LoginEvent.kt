package dev.pastukhov.booking.presentation.model

/**
 * Events for Login screen.
 */
sealed class LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    data object OnTogglePasswordVisibility : LoginEvent()
    data object OnLoginClick : LoginEvent()
    data object ClearError : LoginEvent()
}
