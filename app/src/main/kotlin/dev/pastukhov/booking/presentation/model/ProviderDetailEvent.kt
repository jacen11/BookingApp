package dev.pastukhov.booking.presentation.model

import dev.pastukhov.booking.domain.model.Service

/**
 * Events for Provider Detail Screen.
 */
sealed class ProviderDetailEvent {
    data class SelectService(val service: Service) : ProviderDetailEvent()
    data object LoadProvider : ProviderDetailEvent()
}
