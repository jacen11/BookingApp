package dev.pastukhov.booking.presentation.ui.screens.search

import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory

/**
 * UI state for Search screen.
 */
data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: ProviderCategory? = null,
    val providers: List<Provider> = emptyList(),
    val filteredProviders: List<Provider> = emptyList(),
    val isMapView: Boolean = false,
    val error: String? = null
)
