package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.usecase.GetProvidersUseCase
import dev.pastukhov.booking.domain.usecase.RefreshProvidersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Home Screen.
 */
data class HomeUiState(
    val providers: List<Provider> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: ProviderCategory? = null
)

/**
 * ViewModel for Home Screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProvidersUseCase: GetProvidersUseCase,
    private val refreshProvidersUseCase: RefreshProvidersUseCase
) : BaseViewModel<HomeUiState, Unit>() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProviders()
    }

    /**
     * Load providers from repository.
     */
    fun loadProviders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getProvidersUseCase()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
                .collect { providers ->
                    _uiState.update {
                        it.copy(
                            providers = providers,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * Filter providers by category.
     */
    fun filterByCategory(category: ProviderCategory?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategory = category) }

            val flow = if (category != null) {
                getProvidersUseCase.byCategory(category)
            } else {
                getProvidersUseCase()
            }

            flow.catch { e ->
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }.collect { providers ->
                _uiState.update {
                    it.copy(providers = providers, isLoading = false)
                }
            }
        }
    }

    /**
     * Search providers by query.
     */
    fun search(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            loadProviders()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getProvidersUseCase.search(query)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
                .collect { providers ->
                    _uiState.update {
                        it.copy(providers = providers, isLoading = false)
                    }
                }
        }
    }

    /**
     * Refresh providers from remote.
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            refreshProvidersUseCase()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
        }
    }

    override fun initialState(): HomeUiState = HomeUiState()
}
