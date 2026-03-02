package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.repository.ProviderRepository
import dev.pastukhov.booking.presentation.ui.screens.search.SearchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Search screen.
 * Handles search query, category filtering, and view mode toggle.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val providerRepository: ProviderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadProviders()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterProviders()
    }

    fun onCategorySelected(category: ProviderCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
        filterProviders()
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isMapView = !it.isMapView) }
    }

    private fun loadProviders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                providerRepository.getProviders().collect { providers ->
                    _uiState.update {
                        it.copy(
                            providers = providers,
                            filteredProviders = providers,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun filterProviders() {
        val currentState = _uiState.value
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

        _uiState.update { it.copy(filteredProviders = filtered) }
    }
}