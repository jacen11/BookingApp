package dev.pastukhov.booking.presentation.model

/**
 * Events for Splash screen.
 */
sealed class SplashEvent {
    data object CheckAuth : SplashEvent()
}
