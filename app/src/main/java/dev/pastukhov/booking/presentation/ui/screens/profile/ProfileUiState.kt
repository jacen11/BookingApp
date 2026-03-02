package dev.pastukhov.booking.presentation.ui.screens.profile

import dev.pastukhov.booking.domain.model.AppLanguage
import dev.pastukhov.booking.domain.model.AppTheme
import dev.pastukhov.booking.domain.model.User

/**
 * UI state for Profile screen.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User = emptyUser,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val error: String? = null
)

private val emptyUser: User = User(
    id = "",
    email = "",
    name = "",
    phone = "",
    avatarUrl = "",
)