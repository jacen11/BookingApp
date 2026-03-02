package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.AppLanguage
import dev.pastukhov.booking.domain.model.AppTheme

/**
 * Events for Profile screen.
 */
sealed class ProfileEvent {
    data object LoadUserData : ProfileEvent()
    data object LoadSettings : ProfileEvent()
    data class SetLanguage(val language: AppLanguage) : ProfileEvent()
    data class SetTheme(val theme: AppTheme) : ProfileEvent()
    data class SetNotifications(val enabled: Boolean) : ProfileEvent()
    data object Logout : ProfileEvent()
}
