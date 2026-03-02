package dev.pastukhov.booking.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.presentation.ui.components.EmptyState
import dev.pastukhov.booking.presentation.ui.components.ErrorState
import dev.pastukhov.booking.presentation.ui.components.LoadingIndicator
import dev.pastukhov.booking.presentation.ui.components.RatingComponent
import dev.pastukhov.booking.presentation.ui.components.ServiceCard
import dev.pastukhov.booking.presentation.viewmodel.ProviderDetailUiState
import dev.pastukhov.booking.presentation.viewmodel.ProviderDetailViewModel

/**
 * Provider Detail Screen showing provider info and services.
 */
@Composable
fun ProviderDetailScreen(
    onBookingClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProviderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProviderDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onServiceSelect = { viewModel.selectService(it) },
        onRetry = { viewModel.loadProvider() },
        onBookingClick = onBookingClick
    )
}

/**
 * Stateless version of ProviderDetailScreen for better testability and previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProviderDetailContent(
    uiState: ProviderDetailUiState,
    onBackClick: () -> Unit,
    onServiceSelect: (Service) -> Unit,
    onRetry: () -> Unit,
    onBookingClick: (String, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.provider?.name ?: "",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier.padding(paddingValues)
                )
            }

            uiState.error != null && uiState.provider == null -> {
                ErrorState(
                    message = uiState.error,
                    onRetry = onRetry,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            uiState.provider != null -> {
                val provider = uiState.provider

                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Provider Header
                        item {
                            Column {
                                // Provider Image
                                AsyncImage(
                                    model = provider.imageUrl,
                                    contentDescription = provider.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Provider Name
                                Text(
                                    text = provider.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Category
                                Text(
                                    text = provider.category.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Address
                                Text(
                                    text = provider.address,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                RatingComponent(
                                    rating = provider.rating,
                                    reviewCount = provider.reviewCount
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Services Section Title
                                Text(
                                    text = stringResource(R.string.service),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Services List
                        if (uiState.services.isEmpty()) {
                            item {
                                EmptyState(
                                    message = stringResource(R.string.no_results)
                                )
                            }
                        } else {
                            items(
                                items = uiState.services,
                                key = { it.id }
                            ) { service ->
                                ServiceCard(
                                    service = service,
                                    isSelected = uiState.selectedService?.id == service.id,
                                    onClick = { onServiceSelect(service) }
                                )
                            }
                        }

                        // Add bottom padding to prevent content from being covered by the button
                        if (uiState.selectedService != null) {
                            item {
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                    }

                    // Book Now Button
                    if (uiState.selectedService != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    uiState.provider?.let { provider ->
                                        uiState.selectedService?.let { service ->
                                            onBookingClick(provider.id, service.id)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.book_now),
                                    style = MaterialTheme.typography.titleMedium
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
 * Preview version of ProviderDetailScreen with mock data.
 */
@Preview(showBackground = true)
@Composable
fun ProviderDetailScreenPreview() {
    val mockProvider = Provider(
        id = "1",
        name = "Luxury Spa & Wellness",
        description = "A premium wellness center",
        category = ProviderCategory.BEAUTY,
        imageUrl = null,
        address = "789 Wellness St, Los Angeles, CA",
        city = "Los Angeles",
        rating = 4.9f,
        reviewCount = 450,
        phone = "+1 310 555 0123",
        workingHours = "10:00 - 22:00"
    )

    val mockServices = listOf(
        Service(
            id = "s1",
            providerId = "1",
            name = "Full Body Massage",
            description = "Relaxing 60-minute massage",
            price = 80.0,
            duration = 60
        ),
        Service(
            id = "s2",
            providerId = "1",
            name = "Facial Treatment",
            description = "Rejuvenating skin care",
            price = 65.0,
            duration = 45
        )
    )

    val uiState = ProviderDetailUiState(
        provider = mockProvider,
        services = mockServices,
        selectedService = mockServices[0],
        isLoading = false,
        error = null
    )

    ProviderDetailContent(
        uiState = uiState,
        onBackClick = {},
        onServiceSelect = {},
        onRetry = {},
        onBookingClick = { _, _ -> }
    )
}
