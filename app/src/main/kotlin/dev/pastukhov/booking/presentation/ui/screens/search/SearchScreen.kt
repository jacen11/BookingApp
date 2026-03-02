package dev.pastukhov.booking.presentation.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.presentation.ui.components.ProviderCard
import dev.pastukhov.booking.presentation.viewmodel.SearchEvent
import dev.pastukhov.booking.presentation.viewmodel.SearchViewModel

/**
 * Search screen with list/map toggle and category filters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onProviderClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            Column {
                // Search bar
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.handleEvent(SearchEvent.OnSearchQueryChange(it)) },
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { /* Voice search */ }) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice search")
                        }
                    }
                ) {}

                // Filter chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { viewModel.handleEvent(SearchEvent.OnCategorySelected(null)) },
                            label = { Text(stringResource(R.string.filter_all)) }
                        )
                    }
                    items(
                        listOf(
                            ProviderCategory.SALON to R.string.filter_salon,
                            ProviderCategory.BARBER to R.string.filter_barber,
                            ProviderCategory.SPA to R.string.filter_spa,
                            ProviderCategory.CLINIC to R.string.filter_clinic
                        )
                    ) { (category, labelRes) ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.handleEvent(SearchEvent.OnCategorySelected(category)) },
                            label = { Text(stringResource(labelRes)) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.handleEvent(SearchEvent.OnToggleViewMode) }) {
                Icon(
                    imageVector = if (uiState.isMapView) Icons.AutoMirrored.Filled.List else Icons.Default.Map,
                    contentDescription = if (uiState.isMapView) 
                        stringResource(R.string.view_list) 
                    else 
                        stringResource(R.string.view_map)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: stringResource(R.string.unknown_error),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.filteredProviders.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.empty_search),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                uiState.isMapView -> {
                    MapView(
                        providers = uiState.filteredProviders,
                        onProviderClick = onProviderClick
                    )
                }
                else -> {
                    ListView(
                        providers = uiState.filteredProviders,
                        onProviderClick = onProviderClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ListView(
    providers: List<Provider>,
    onProviderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(providers, key = { it.id }) { provider ->
            ProviderCard(
                provider = provider,
                onClick = { onProviderClick(provider.id) }
            )
        }
    }
}

@Composable
private fun MapView(
    providers: List<Provider>,
    onProviderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val mexicoCity = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mexicoCity, 12f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        providers.forEach { provider ->
            Marker(
                state = MarkerState(
                    position = LatLng(provider.latitude, provider.longitude)
                ),
                title = provider.name,
                snippet = "${provider.rating} ⭐ • ${provider.priceRange}",
                onInfoWindowClick = {
                    onProviderClick(provider.id)
                }
            )
        }
    }
}
