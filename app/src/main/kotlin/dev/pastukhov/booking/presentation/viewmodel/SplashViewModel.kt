package dev.pastukhov.booking.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.presentation.model.SplashDestination
import dev.pastukhov.booking.presentation.model.SplashEvent
import dev.pastukhov.booking.presentation.model.SplashUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * ViewModel for Splash Screen.
 * Handles authentication check and determines navigation destination.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<SplashUiState, SplashEvent>() {

    // Minimum splash display duration to ensure user sees the logo animation
    companion object {
        const val MIN_SPLASH_DURATION_MS = 1500L
    }

    override fun initialState(): SplashUiState = SplashUiState()

    init {
        handleEvent(SplashEvent.CheckAuth)
    }

    override fun handleEvent(event: SplashEvent) {
        when (event) {
            is SplashEvent.CheckAuth -> checkAuthAndNavigate()
        }
    }

    /**
     * Check authentication status and determine navigation destination.
     * Ensures minimum splash duration for smooth UX.
     */
    private fun checkAuthAndNavigate() {
        launchWithErrorHandling(
            onError = { throwable ->
                // Navigate to login on error as fallback
                updateState {
                    copy(
                        isLoading = false,
                        destination = SplashDestination.Login
                    )
                }
            }
        ) {
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
            updateState {
                copy(
                    isLoading = false,
                    destination = if (isLoggedIn) SplashDestination.Home else SplashDestination.Login
                )
            }
        }
    }
}
