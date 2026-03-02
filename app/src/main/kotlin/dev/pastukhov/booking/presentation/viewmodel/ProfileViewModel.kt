package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.domain.model.AppLanguage
import dev.pastukhov.booking.domain.model.AppTheme
import dev.pastukhov.booking.domain.repository.UserRepository
import dev.pastukhov.booking.presentation.ui.screens.profile.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

/**
 * ViewModel for Profile screen.
 * Handles user data, settings, and logout.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository
) : BaseViewModel<ProfileUiState, ProfileEvent>() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadSettings()
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                userRepository.getCurrentUser().collect { user ->
                    _uiState.update { it.copy(user = user!!, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.userSettings.collect { settings ->
                _uiState.update {
                    it.copy(
                        language = settings.language,
                        theme = settings.theme,
                        notificationsEnabled = settings.notificationsEnabled
                    )
                }
            }
        }
    }

    private fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }

    private fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    private fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotifications(enabled)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            settingsRepository.clearSettings()
            // Clear user session would go here
        }
    }
}