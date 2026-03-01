package dev.pastukhov.booking.presentation.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.domain.model.AppLanguage
import dev.pastukhov.booking.domain.model.AppTheme
import dev.pastukhov.booking.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Profile screen.
 * Handles user data, settings, and logout.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadSettings()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                userRepository.getCurrentUser().collect { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
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

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotifications(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            settingsRepository.clearSettings()
            // Clear user session would go here
        }
    }
}
