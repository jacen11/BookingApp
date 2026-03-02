package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory

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
