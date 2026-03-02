package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

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

/**
 * ViewModel for Splash Screen.
 * Handles authentication check and determines navigation destination.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    // Minimum splash display duration to ensure user sees the logo animation
    companion object {
        const val MIN_SPLASH_DURATION_MS = 1500L
    }

    init {
        checkAuthAndNavigate()
    }

    /**
     * Check authentication status and determine navigation destination.
     * Ensures minimum splash duration for smooth UX.
     */
    private fun checkAuthAndNavigate() {
        viewModelScope.launch {
            // Record start time for minimum splash duration
            val startTime = System.currentTimeMillis()

            // Check if user is logged in via DataStore
            val isLoggedIn = userSettingsRepository.isLoggedIn().first()

            // Calculate elapsed time
            val elapsedTime = System.currentTimeMillis() - startTime

            // Ensure minimum splash duration for better UX
            val remainingTime = MIN_SPLASH_DURATION_MS - elapsedTime
            if (remainingTime > 0) {
                delay(remainingTime)
            }

            // Navigate based on auth status
            _uiState.value = SplashUiState(
                isLoading = false,
                destination = if (isLoggedIn) SplashDestination.Home else SplashDestination.Login
            )
        }
    }
}
