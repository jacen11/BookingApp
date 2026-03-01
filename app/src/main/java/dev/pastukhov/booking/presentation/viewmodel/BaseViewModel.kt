package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel providing common functionality for all ViewModels.
 * Handles UI state management and error handling.
 */
abstract class BaseViewModel<State, Event> : ViewModel() {

    // UI State
    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<State> = _state.asStateFlow()

    /**
     * Provides initial state for the ViewModel.
     */
    protected abstract fun initialState(): State

    /**
     * Updates the current state.
     */
    protected fun updateState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    /**
     * Launches a coroutine with error handling.
     */
    protected fun launchWithErrorHandling(
        onError: ((Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                onError?.invoke(throwable)
            },
            block = block
        )
    }
}

/**
 * Generic UI state wrapper for handling Loading, Success, and Error states.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = (this as? Success)?.data

    fun <R> map(transform: (T) -> R): UiState<R> {
        return when (this) {
            is Loading -> Loading
            is Success -> Success(transform(data))
            is Error -> Error(message, throwable)
        }
    }
}

/**
 * Extension function to convert Result to UiState.
 */
fun <T> Result<T>.toUiState(): UiState<T> {
    return fold(
        onSuccess = { UiState.Success(it) },
        onFailure = { UiState.Error(it.message ?: "Unknown error", it) }
    )
}
