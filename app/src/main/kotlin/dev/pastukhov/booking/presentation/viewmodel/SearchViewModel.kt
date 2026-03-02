package dev.pastukhov.booking.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.repository.ProviderRepository
import dev.pastukhov.booking.presentation.ui.screens.search.SearchUiState
import javax.inject.Inject

/**
 * Events for Search screen.
 */
sealed class SearchEvent {
    data class OnSearchQueryChange(val query: String) : SearchEvent()
    data class OnCategorySelected(val category: ProviderCategory?) : SearchEvent()
    data object OnToggleViewMode : SearchEvent()
    data object OnLoadProviders : SearchEvent()
}

/**
 * ViewModel for Search screen.
 * Handles search query, category filtering, and view mode toggle.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val providerRepository: ProviderRepository
) : BaseViewModel<SearchUiState, SearchEvent>() {

    override fun initialState(): SearchUiState = SearchUiState()

    init {
        handleEvent(SearchEvent.OnLoadProviders)
    }

    override fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnSearchQueryChange -> onSearchQueryChange(event.query)
            is SearchEvent.OnCategorySelected -> onCategorySelected(event.category)
            is SearchEvent.OnToggleViewMode -> toggleViewMode()
            is SearchEvent.OnLoadProviders -> loadProviders()
        }
    }

    private fun onSearchQueryChange(query: String) {
        updateState { copy(searchQuery = query) }
        filterProviders()
    }

    private fun onCategorySelected(category: ProviderCategory?) {
        updateState { copy(selectedCategory = category) }
        filterProviders()
    }

    private fun toggleViewMode() {
        updateState { copy(isMapView = !isMapView) }
    }

    private fun loadProviders() {
        launchWithErrorHandling(
            onError = { throwable ->
                updateState {
                    copy(
                        isLoading = false,
                        error = throwable.message
                    )
                }
            }
        ) {
            updateState { copy(isLoading = true) }
            providerRepository.getProviders().collect { providers ->
                updateState {
                    copy(
                        providers = providers,
                        filteredProviders = providers,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun filterProviders() {
        val currentState = state.value
        val filtered = currentState.providers.filter { provider ->
            val matchesQuery = if (currentState.searchQuery.isBlank()) {
                true
            } else {
                provider.name.contains(currentState.searchQuery, ignoreCase = true) ||
                        provider.description.contains(currentState.searchQuery, ignoreCase = true)
            }

            val matchesCategory = currentState.selectedCategory?.let {
                provider.category == it
            } ?: true

            matchesQuery && matchesCategory
        }

        updateState { copy(filteredProviders = filtered) }
    }
}
