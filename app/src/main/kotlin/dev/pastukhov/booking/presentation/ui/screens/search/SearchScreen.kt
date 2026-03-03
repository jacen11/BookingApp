package dev.pastukhov.booking.presentation.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.presentation.ui.screens.search.component.SearchListView
import dev.pastukhov.booking.presentation.ui.screens.search.component.SearchMapView
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

    SearchScreenContent(
        isLoading = uiState.isLoading,
        searchQuery = uiState.searchQuery,
        selectedCategory = uiState.selectedCategory,
        filteredProviders = uiState.filteredProviders,
        isMapView = uiState.isMapView,
        error = uiState.error,
        onSearchQueryChange = { viewModel.handleEvent(SearchEvent.OnSearchQueryChange(it)) },
        onCategorySelected = { viewModel.handleEvent(SearchEvent.OnCategorySelected(it)) },
        onToggleViewMode = { viewModel.handleEvent(SearchEvent.OnToggleViewMode) },
        onProviderClick = onProviderClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    isLoading: Boolean,
    searchQuery: String,
    selectedCategory: ProviderCategory?,
    filteredProviders: List<Provider>,
    isMapView: Boolean,
    error: String?,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (ProviderCategory?) -> Unit,
    onToggleViewMode: () -> Unit,
    onProviderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Column {
                // Search bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
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
                            selected = selectedCategory == null,
                            onClick = { onCategorySelected(null) },
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
                            selected = selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(stringResource(labelRes)) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onToggleViewMode) {
                Icon(
                    imageVector = if (isMapView) Icons.AutoMirrored.Filled.List else Icons.Default.Map,
                    contentDescription = if (isMapView)
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
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                filteredProviders.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.empty_search),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                isMapView -> {
                    SearchMapView(
                        providers = filteredProviders,
                        onProviderClick = onProviderClick
                    )
                }
                else -> {
                    SearchListView(
                        providers = filteredProviders,
                        onProviderClick = onProviderClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenContentPreview() {
    val sampleProviders = listOf(
        Provider(
            id = "1",
            name = "Salón de Belleza Luxe",
            description = "Centro de belleza y spa",
            category = ProviderCategory.SALON,
            address = "Av. Principal 123",
            city = "Ciudad de México",
            rating = 4.5f,
            reviewCount = 128,
            phone = "+52 55 1234 5678",
            workingHours = "9:00 - 20:00",
            priceRange = "$$"
        ),
        Provider(
            id = "2",
            name = "Barbería Clásica",
            description = "Corte y afeitado tradicional",
            category = ProviderCategory.BARBER,
            address = "Calle Secundaria 45",
            city = "Ciudad de México",
            rating = 4.8f,
            reviewCount = 256,
            phone = "+52 55 8765 4321",
            workingHours = "10:00 - 21:00",
            priceRange = "$"
        ),
        Provider(
            id = "3",
            name = "Spa Wellness",
            description = "Tratamientos de relajación",
            category = ProviderCategory.SPA,
            address = "Blvd. Salud 78",
            city = "Ciudad de México",
            rating = 4.9f,
            reviewCount = 89,
            phone = "+52 55 5555 5555",
            workingHours = "8:00 - 22:00",
            priceRange = "$$$"
        )
    )

    MaterialTheme {
        SearchScreenContent(
            isLoading = false,
            searchQuery = "",
            selectedCategory = null,
            filteredProviders = sampleProviders,
            isMapView = false,
            error = null,
            onSearchQueryChange = {},
            onCategorySelected = {},
            onToggleViewMode = {},
            onProviderClick = {}
        )
    }
}
