package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.ProviderCategory

/**
 * Events for Search screen.
 */
sealed class SearchEvent {
    data class OnSearchQueryChange(val query: String) : SearchEvent()
    data class OnCategorySelected(val category: ProviderCategory?) : SearchEvent()
    data object OnToggleViewMode : SearchEvent()
    data object OnLoadProviders : SearchEvent()
}
