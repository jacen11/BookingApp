package dev.pastukhov.booking.presentation.model

/**
 * Sealed class representing navigation destinations after splash screen.
 */
sealed class SplashDestination {
    data object Home : SplashDestination()
    data object Login : SplashDestination()
}

/**
 * UI State for Splash Screen.
 */
data class SplashUiState(
    val isLoading: Boolean = true,
    val destination: SplashDestination? = null
)
