package dev.pastukhov.booking.presentation.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.presentation.ui.components.EmptyState
import dev.pastukhov.booking.presentation.ui.components.ErrorState
import dev.pastukhov.booking.presentation.ui.components.ProviderCard
import dev.pastukhov.booking.presentation.viewmodel.HomeEvent
import dev.pastukhov.booking.presentation.viewmodel.HomeUiState
import dev.pastukhov.booking.presentation.viewmodel.HomeViewModel

/**
 * Home screen displaying list of providers with search and filter.
 */
@Composable
fun HomeScreen(
    onProviderClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onSearch = { viewModel.handleEvent(HomeEvent.Search(it)) },
        onFilterByCategory = { viewModel.handleEvent(HomeEvent.FilterByCategory(it)) },
        onRefresh = { viewModel.handleEvent(HomeEvent.Refresh) },
        onLoadProviders = { viewModel.handleEvent(HomeEvent.LoadProviders) },
        onProviderClick = onProviderClick
    )
}

/**
 * Stateless version of HomeScreen for better testability and previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onSearch: (String) -> Unit,
    onFilterByCategory: (ProviderCategory?) -> Unit,
    onRefresh: () -> Unit,
    onLoadProviders: () -> Unit,
    onProviderClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true
            )

            // Category filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" chip
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { onFilterByCategory(null) },
                        label = { Text(stringResource(R.string.category_all)) }
                    )
                }

                // Category chips
                items(ProviderCategory.entries.toTypedArray()) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onFilterByCategory(category) },
                        label = { Text(category.displayName) }
                    )
                }
            }

            // Content
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.error != null && uiState.providers.isEmpty() -> {
                        ErrorState(
                            message = uiState.error ?: "",
                            onRetry = onLoadProviders
                        )
                    }

                    uiState.providers.isEmpty() && !uiState.isLoading -> {
                        EmptyState(
                            message = stringResource(R.string.no_providers_found)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.providers,
                                key = { it.id }
                            ) { provider ->
                                ProviderCard(
                                    provider = provider,
                                    onClick = { onProviderClick(provider.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Preview version of HomeScreen with mock data.
 */
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockProviders = listOf(
        Provider(
            id = "1",
            name = "Beauty Studio",
            description = "Professional beauty services",
            category = ProviderCategory.BEAUTY,
            imageUrl = null,
            address = "123 Main St",
            city = "New York",
            rating = 4.5f,
            reviewCount = 120,
            phone = "+1 234 567 8900",
            workingHours = "9:00 - 18:00"
        ),
        Provider(
            id = "2",
            name = "Dental Care Clinic",
            description = "Best dental care in town",
            category = ProviderCategory.CLINIC,
            imageUrl = null,
            address = "456 Oak Ave",
            city = "Los Angeles",
            rating = 4.8f,
            reviewCount = 250,
            phone = "+1 234 567 8901",
            workingHours = "8:00 - 20:00"
        )
    )

    val uiState = HomeUiState(
        providers = mockProviders,
        isLoading = false,
        error = null,
        searchQuery = "",
        selectedCategory = null
    )

    HomeScreenContent(
        uiState = uiState,
        onSearch = {},
        onFilterByCategory = {},
        onRefresh = {},
        onLoadProviders = {},
        onProviderClick = {}
    )
}
