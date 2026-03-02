package dev.pastukhov.booking.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.domain.repository.UserRepository
import dev.pastukhov.booking.presentation.model.ProfileEvent
import dev.pastukhov.booking.presentation.model.ProfileUiState
import javax.inject.Inject

/**
 * ViewModel for Profile screen.
 * Handles user data, settings, and logout.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository
) : BaseViewModel<ProfileUiState, ProfileEvent>() {

    init {
        handleEvent(ProfileEvent.LoadUserData)
        handleEvent(ProfileEvent.LoadSettings)
    }

    override fun initialState(): ProfileUiState = ProfileUiState()

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadUserData -> loadUserData()
            is ProfileEvent.LoadSettings -> loadSettings()
            is ProfileEvent.SetLanguage -> setLanguage(event.language)
            is ProfileEvent.SetTheme -> setTheme(event.theme)
            is ProfileEvent.SetNotifications -> setNotifications(event.enabled)
            is ProfileEvent.Logout -> logout()
        }
    }

    private fun loadUserData() {
        launchWithErrorHandling(
            onError = { exception ->
                updateState { copy(isLoading = false, error = exception.message) }
            }
        ) {
            updateState { copy(isLoading = true) }
            userRepository.getCurrentUser().collect { user ->
                updateState { copy(user = user!!, isLoading = false) }
            }
        }
    }

    private fun loadSettings() {
        launchWithErrorHandling {
            settingsRepository.userSettings.collect { settings ->
                updateState {
                    copy(
                        language = settings.language,
                        theme = settings.theme,
                        notificationsEnabled = settings.notificationsEnabled
                    )
                }
            }
        }
    }

    private fun setLanguage(language: dev.pastukhov.booking.domain.model.AppLanguage) {
        launchWithErrorHandling {
            settingsRepository.setLanguage(language)
        }
    }

    private fun setTheme(theme: dev.pastukhov.booking.domain.model.AppTheme) {
        launchWithErrorHandling {
            settingsRepository.setTheme(theme)
        }
    }

    private fun setNotifications(enabled: Boolean) {
        launchWithErrorHandling {
            settingsRepository.setNotifications(enabled)
        }
    }

    private fun logout() {
        launchWithErrorHandling {
            settingsRepository.clearSettings()
        }
    }
}