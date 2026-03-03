package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service

/**
 * UI State for Provider Detail Screen.
 */
data class ProviderDetailUiState(
    val provider: Provider? = null,
    val services: List<Service> = emptyList(),
    val selectedService: Service? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
