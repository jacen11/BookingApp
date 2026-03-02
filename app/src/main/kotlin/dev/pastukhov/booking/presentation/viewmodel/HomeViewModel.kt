package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.usecase.GetProvidersUseCase
import dev.pastukhov.booking.domain.usecase.RefreshProvidersUseCase
import dev.pastukhov.booking.presentation.model.HomeEvent
import dev.pastukhov.booking.presentation.model.HomeUiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home Screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProvidersUseCase: GetProvidersUseCase,
    private val refreshProvidersUseCase: RefreshProvidersUseCase
) : BaseViewModel<HomeUiState, HomeEvent>() {

    init {
        handleEvent(HomeEvent.LoadProviders)
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadProviders -> loadProviders()
            is HomeEvent.Search -> search(event.query)
            is HomeEvent.FilterByCategory -> filterByCategory(event.category)
            is HomeEvent.Refresh -> refresh()
        }
    }

    /**
     * Load providers from repository.
     */
    private fun loadProviders() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            getProvidersUseCase()
                .catch { e ->
                    updateState {
                        copy(isLoading = false, error = e.message)
                    }
                }
                .collect { providers ->
                    updateState {
                        copy(
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
    private fun filterByCategory(category: ProviderCategory?) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, selectedCategory = category) }

            val flow = if (category != null) {
                getProvidersUseCase.byCategory(category)
            } else {
                getProvidersUseCase()
            }

            flow.catch { e ->
                updateState {
                    copy(isLoading = false, error = e.message)
                }
            }.collect { providers ->
                updateState {
                    copy(providers = providers, isLoading = false)
                }
            }
        }
    }

    /**
     * Search providers by query.
     */
    private fun search(query: String) {
        updateState { copy(searchQuery = query) }

        if (query.isBlank()) {
            loadProviders()
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            getProvidersUseCase.search(query)
                .catch { e ->
                    updateState {
                        copy(isLoading = false, error = e.message)
                    }
                }
                .collect { providers ->
                    updateState {
                        copy(providers = providers, isLoading = false)
                    }
                }
        }
    }

    /**
     * Refresh providers from remote.
     */
    private fun refresh() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            refreshProvidersUseCase()
                .onSuccess {
                    updateState { copy(isLoading = false) }
                }
                .onFailure { e ->
                    updateState {
                        copy(isLoading = false, error = e.message)
                    }
                }
        }
    }

    override fun initialState(): HomeUiState = HomeUiState()
}
