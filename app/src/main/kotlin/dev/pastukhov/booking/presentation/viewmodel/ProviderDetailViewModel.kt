package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.usecase.GetProvidersUseCase
import dev.pastukhov.booking.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

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

/**
 * Events for Provider Detail Screen.
 */
sealed class ProviderDetailEvent {
    data class SelectService(val service: Service) : ProviderDetailEvent()
    data object LoadProvider : ProviderDetailEvent()
}

/**
 * ViewModel for Provider Detail Screen.
 */
@HiltViewModel
class ProviderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProvidersUseCase: GetProvidersUseCase,
    private val getServicesUseCase: GetServicesUseCase
) : BaseViewModel<ProviderDetailUiState, ProviderDetailEvent>() {

    private val providerId: String = savedStateHandle.get<String>("providerId") ?: ""

    init {
        handleEvent(ProviderDetailEvent.LoadProvider)
    }

    override fun initialState(): ProviderDetailUiState = ProviderDetailUiState()

    override fun handleEvent(event: ProviderDetailEvent) {
        when (event) {
            is ProviderDetailEvent.LoadProvider -> loadProvider()
            is ProviderDetailEvent.SelectService -> selectService(event.service)
        }
    }

    /**
     * Load provider details and services.
     */
    private fun loadProvider() {
        launchWithErrorHandling(
            onError = { e -> updateState { copy(isLoading = false, error = e.message) } }
        ) {
            updateState { copy(isLoading = true, error = null) }

            // Load provider
            getProvidersUseCase.byId(providerId)
                .catch { e -> updateState { copy(isLoading = false, error = e.message) } }
                .collect { provider ->
                    updateState { copy(provider = provider, isLoading = false) }
                }
        }

        // Load services for this provider
        launchWithErrorHandling(
            onError = { e -> updateState { copy(error = e.message) } }
        ) {
            getServicesUseCase(providerId)
                .catch { e -> updateState { copy(error = e.message) } }
                .collect { services ->
                    updateState { copy(services = services) }
                }
        }
    }

    /**
     * Select a service for booking.
     */
    private fun selectService(service: Service) {
        updateState { copy(selectedService = service) }
    }
}
