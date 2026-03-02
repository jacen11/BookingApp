package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.ProviderCategory

/**
 * Events for Home screen.
 */
sealed class HomeEvent {
    data object LoadProviders : HomeEvent()
    data class Search(val query: String) : HomeEvent()
    data class FilterByCategory(val category: ProviderCategory?) : HomeEvent()
    data object Refresh : HomeEvent()
}
