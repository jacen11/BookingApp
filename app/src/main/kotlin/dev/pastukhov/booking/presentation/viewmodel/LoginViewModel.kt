package dev.pastukhov.booking.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.presentation.model.LoginEvent
import dev.pastukhov.booking.presentation.model.LoginUiState
import javax.inject.Inject

/**
 * ViewModel for Login screen.
 * Handles user authentication and login state.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<LoginUiState, LoginEvent>() {

    override fun initialState(): LoginUiState = LoginUiState()

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> updateState { copy(email = event.email) }
            is LoginEvent.OnPasswordChanged -> updateState { copy(password = event.password) }
            is LoginEvent.OnTogglePasswordVisibility -> updateState { copy(isPasswordVisible = !isPasswordVisible) }
            is LoginEvent.OnLoginClick -> performLogin()
            is LoginEvent.ClearError -> updateState { copy(error = null) }
        }
    }

    private fun performLogin() {
        val currentState = state.value

        // Validate input
        if (currentState.email.isBlank()) {
            updateState { copy(error = "Email is required") }
            return
        }

        if (currentState.password.isBlank()) {
            updateState { copy(error = "Password is required") }
            return
        }

        launchWithErrorHandling(
            onError = { exception ->
                updateState { copy(isLoading = false, error = exception.message) }
            }
        ) {
            updateState { copy(isLoading = true) }

            // Generate mock token and save to DataStore
            userSettingsRepository.saveAuthToken(
                token = "mock_token_${System.currentTimeMillis()}",
                userId = currentState.email.ifEmpty { "user_1" }
            )

            updateState { copy(isLoading = false, isLoginSuccessful = true) }
        }
    }
}
