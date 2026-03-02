package dev.pastukhov.booking.presentation.ui.screens.mybookings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.presentation.ui.components.BookingItemCard
import dev.pastukhov.booking.presentation.viewmodel.BookingTab
import dev.pastukhov.booking.presentation.viewmodel.MyBookingsUiState
import dev.pastukhov.booking.presentation.viewmodel.MyBookingsViewModel

/**
 * My Bookings screen with tabs for Active, History, and Cancelled bookings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onNavigateBack: () -> Unit,
    onBookNow: () -> Unit,
    onRepeatBooking: (Booking) -> Unit,
    onRateService: (Booking) -> Unit,
    viewModel: MyBookingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_bookings),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            BookingTabs(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )

            // Content
            AnimatedContent(
                targetState = uiState.selectedTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tab_content"
            ) { tab ->
                when {
                    uiState.isLoading -> {
                        LoadingContent()
                    }
                    uiState.error != null -> {
                        ErrorContent(
                            error = uiState.error!!,
                            onRetry = { viewModel.loadBookings() }
                        )
                    }
                    uiState.isEmpty -> {
                        EmptyContent(onBookNow = onBookNow)
                    }
                    else -> {
                        BookingList(
                            bookings = uiState.currentBookings,
                            tab = uiState.selectedTab,
                            onCancelClick = { booking -> viewModel.showCancelDialog(booking) },
                            onCallClick = { phone -> dialPhone(context, phone) },
                            onMessageClick = { phone -> /* TODO: Implement message */ },
                            onRepeatClick = onRepeatBooking,
                            onRateClick = onRateService
                        )
                    }
                }
            }
        }
    }

    // Cancel confirmation dialog
    if (uiState.showCancelDialog) {
        CancelBookingDialog(
            isCancelling = uiState.isCancelling,
            onConfirm = { viewModel.confirmCancelBooking() },
            onDismiss = { viewModel.dismissCancelDialog() }
        )
    }
}

/**
 * Tab row for booking categories.
 */
@Composable
private fun BookingTabs(
    selectedTab: BookingTab,
    onTabSelected: (BookingTab) -> Unit
) {
    val tabs = listOf(
        BookingTab.ACTIVE to stringResource(R.string.tab_active),
        BookingTab.HISTORY to stringResource(R.string.tab_history),
        BookingTab.CANCELLED to stringResource(R.string.tab_cancelled)
    )

    TabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabs.forEachIndexed { index, (tab, title) ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

/**
 * Loading state with shimmer effect.
 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Error state with retry button.
 */
@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.error),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

/**
 * Empty state with illustration and action button.
 */
@Composable
private fun EmptyContent(
    onBookNow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.empty_bookings),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_bookings_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBookNow,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.empty_bookings_action))
        }
    }
}

/**
 * Booking list with cards.
 */
@Composable
private fun BookingList(
    bookings: List<Booking>,
    tab: BookingTab,
    onCancelClick: (Booking) -> Unit,
    onCallClick: (String) -> Unit,
    onMessageClick: (String) -> Unit,
    onRepeatClick: (Booking) -> Unit,
    onRateClick: (Booking) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = bookings,
            key = { it.id }
        ) { booking ->
            BookingItemCard(
                booking = booking,
                tab = tab,
                onCancelClick = { onCancelClick(booking) },
                onCallClick = onCallClick,
                onMessageClick = onMessageClick,
                onRepeatClick = { onRepeatClick(booking) },
                onRateClick = { onRateClick(booking) },
                providerPhone = "+52 55 1234 5678" // TODO: Get from provider
            )
        }
    }
}

/**
 * Cancel booking confirmation dialog.
 */
@Composable
private fun CancelBookingDialog(
    isCancelling: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isCancelling) onDismiss() },
        title = {
            Text(
                text = stringResource(R.string.cancel_booking_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(stringResource(R.string.cancel_booking_message))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isCancelling
            ) {
                if (isCancelling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.yes_cancel))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isCancelling
            ) {
                Text(stringResource(R.string.no_keep))
            }
        }
    )
}

/**
 * Open phone dialer with the given phone number.
 */
private fun dialPhone(context: android.content.Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
    }
    context.startActivity(intent)
}
