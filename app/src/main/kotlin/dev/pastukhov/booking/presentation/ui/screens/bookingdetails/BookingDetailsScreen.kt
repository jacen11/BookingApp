package dev.pastukhov.booking.presentation.ui.screens.bookingdetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import dev.pastukhov.booking.domain.model.PaymentMethod
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.presentation.model.BookingDetailsData
import dev.pastukhov.booking.presentation.model.BookingDetailsEvent
import dev.pastukhov.booking.presentation.ui.components.CancelBookingDialog
import dev.pastukhov.booking.presentation.viewmodel.BookingDetailsViewModel
import java.time.LocalDate
import java.time.LocalTime

/**
 * Booking Details screen showing complete information about a booking.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProvider: (String) -> Unit,
    onNavigateToRepeatBooking: (String, String) -> Unit,
    onNavigateToRate: (String) -> Unit,
    viewModel: BookingDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Load booking on first composition
    LaunchedEffect(bookingId) {
        viewModel.handleEvent(BookingDetailsEvent.LoadBooking(bookingId))
    }

    // Handle share intent
    LaunchedEffect(uiState.shareText) {
        uiState.shareText?.let { text ->
            shareBooking(context, text)
            viewModel.clearShareText()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.booking_details),
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
                actions = {
                    IconButton(onClick = { viewModel.handleEvent(BookingDetailsEvent.ShareBooking) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share_booking)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error!!,
                        onRetry = { viewModel.handleEvent(BookingDetailsEvent.LoadBooking(bookingId)) }
                    )
                }

                uiState.booking != null -> {
                    BookingDetailsContent(
                        bookingData = uiState.booking!!,
                        onCancelClick = { viewModel.handleEvent(BookingDetailsEvent.ShowCancelDialog) },
                        onCallClick = { phone -> dialPhone(context, phone) },
                        onWhatsAppClick = { phone -> openWhatsApp(context, phone) },
                        onRepeatClick = {
                            onNavigateToRepeatBooking(
                                uiState.booking!!.booking.providerId,
                                uiState.booking!!.booking.serviceId
                            )
                        },
                        onRateClick = { onNavigateToRate(bookingId) },
                        onProviderClick = { onNavigateToProvider(uiState.booking!!.booking.providerId) }
                    )
                }
            }
        }
    }

    // Cancel confirmation dialog
    if (uiState.showCancelDialog) {
        CancelBookingDialog(
            isCancelling = uiState.isCancelling,
            onConfirm = { viewModel.handleEvent(BookingDetailsEvent.ConfirmCancelBooking) },
            onDismiss = { viewModel.handleEvent(BookingDetailsEvent.DismissCancelDialog) }
        )
    }

    // Rating dialog (placeholder)
    if (uiState.showRatingDialog) {
        RatingDialog(
            isRating = uiState.isRating,
            onSubmit = { rating, comment ->
                viewModel.handleEvent(BookingDetailsEvent.SubmitRating(rating, comment))
            },
            onDismiss = { viewModel.handleEvent(BookingDetailsEvent.DismissRatingDialog) }
        )
    }
}


/**
 * Main booking details content.
 */
@Composable
private fun BookingDetailsContent(
    bookingData: BookingDetailsData,
    onCancelClick: () -> Unit,
    onCallClick: (String) -> Unit,
    onWhatsAppClick: (String) -> Unit,
    onRepeatClick: () -> Unit,
    onRateClick: () -> Unit,
    onProviderClick: () -> Unit
) {
    val booking = bookingData.booking
    val provider = bookingData.provider

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Status Badge
        StatusBadge(
            status = booking.status,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Main content
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // Service Info Card
            ServiceInfoCard(
                serviceName = booking.serviceName,
                price = booking.totalPrice,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Provider Info Card
            ProviderInfoCard(
                providerName = booking.providerName,
                providerAddress = booking.providerAddress,
                rating = provider?.rating ?: 0f,
                reviewCount = provider?.reviewCount ?: 0,
                phone = provider?.phone ?: "",
                onProviderClick = onProviderClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Time Card
            DateTimeCard(
                dateTime = bookingData.formattedDateTime(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Status Card
            PaymentStatusCard(
                isPaid = booking.isPaid,
                paymentMethod = booking.paymentMethod?.displayName ?: "",
                modifier = Modifier.fillMaxWidth()
            )

            // Notes (if any)
            if (!booking.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                NotesCard(
                    notes = booking.notes,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons based on status
            ActionButtons(
                isActive = bookingData.isActive,
                isCompleted = bookingData.isCompleted,
                isCancelled = bookingData.isCancelled,
                onCancelClick = onCancelClick,
                onCallClick = { onCallClick(provider?.phone ?: "") },
                onWhatsAppClick = { onWhatsAppClick(provider?.phone ?: "") },
                onRepeatClick = onRepeatClick,
                onRateClick = onRateClick
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


/**
 * Open phone dialer.
 */
private fun dialPhone(context: Context, phone: String) {
    if (phone.isBlank()) return
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
    }
    context.startActivity(intent)
}

/**
 * Open WhatsApp with the given phone number.
 */
private fun openWhatsApp(context: Context, phone: String) {
    if (phone.isBlank()) return
    try {
        // Remove any non-digit characters
        val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // WhatsApp not installed, open phone dialer instead
        dialPhone(context, phone)
    }
}

/**
 * Share booking details.
 */
private fun shareBooking(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share booking"))
}

@Preview(showBackground = true)
@Composable
fun BookingDetailsContentPreview() {
    MaterialTheme {
        val booking = Booking(
            id = "12345",
            userId = "user_001",
            providerId = "provider_001",
            providerName = "Salón de Belleza Juan",
            providerAddress = "Av. Principal 123, Ciudad",
            serviceId = "service_001",
            serviceName = "Corte de cabello",
            date = LocalDate.of(2025, 1, 25),
            time = LocalTime.of(14, 30),
            status = BookingStatus.CONFIRMED,
            totalPrice = 25.0,
            notes = "Por favor llegar 10 minutos antes.",
            paymentMethod = PaymentMethod.CARD,
            isPaid = true
        )
        val provider = Provider(
            id = "provider_001",
            name = "Salón de Belleza Juan",
            description = "Mejor salón de la ciudad",
            category = ProviderCategory.SALON,
            imageUrl = null,
            address = "Av. Principal 123, Ciudad",
            city = "Ciudad",
            rating = 4.5f,
            reviewCount = 128,
            phone = "+52 123 456 7890",
            workingHours = "9:00 - 20:00",
            latitude = 0.0,
            longitude = 0.0,
            priceRange = "$"
        )
        val service = Service(
            id = "service_001",
            providerId = "provider_001",
            name = "Corte de cabello",
            description = "Corte de cabello clásico",
            price = 25.0,
            currency = "USD",
            duration = 30,
            imageUrl = null
        )

        BookingDetailsContent(
            bookingData = BookingDetailsData(booking = booking, provider = provider, service = service),
            onCancelClick = {},
            onCallClick = {},
            onWhatsAppClick = {},
            onRepeatClick = {},
            onRateClick = {},
            onProviderClick = {}
        )
    }
}
