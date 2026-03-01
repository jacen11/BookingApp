package dev.pastukhov.booking.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.usecase.GetProvidersUseCase
import dev.pastukhov.booking.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
 * ViewModel for Provider Detail Screen.
 */
@HiltViewModel
class ProviderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProvidersUseCase: GetProvidersUseCase,
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {

    private val providerId: String = savedStateHandle.get<String>("providerId") ?: ""

    private val _uiState = MutableStateFlow(ProviderDetailUiState())
    val uiState: StateFlow<ProviderDetailUiState> = _uiState.asStateFlow()

    init {
        loadProvider()
    }

    /**
     * Load provider details and services.
     */
    fun loadProvider() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load provider
            getProvidersUseCase.byId(providerId)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
                .collect { provider ->
                    _uiState.update {
                        it.copy(provider = provider, isLoading = false)
                    }
                }
        }

        // Load services for this provider
        viewModelScope.launch {
            getServicesUseCase(providerId)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message)
                    }
                }
                .collect { services ->
                    _uiState.update {
                        it.copy(services = services)
                    }
                }
        }
    }

    /**
     * Select a service for booking.
     */
    fun selectService(service: Service) {
        _uiState.update { it.copy(selectedService = service) }
    }
}
