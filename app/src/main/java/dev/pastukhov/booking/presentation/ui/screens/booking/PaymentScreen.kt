package dev.pastukhov.booking.presentation.ui.screens.booking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.PaymentMethod
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.presentation.ui.screens.booking.component.CardDetailsSection
import dev.pastukhov.booking.presentation.ui.screens.booking.component.PaymentMethodsSection
import dev.pastukhov.booking.presentation.ui.screens.booking.component.SecurityNotice
import dev.pastukhov.booking.presentation.ui.screens.booking.component.TotalSection

/**
 * Screen 3: Payment
 * Allows user to select payment method and enter card details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    PaymentContent(
        uiState = uiState,
        onBack = onBack,
        onSelectPaymentMethod = { viewModel.selectPaymentMethod(it) },
        onCardNumberChange = { viewModel.updateCardNumber(it) },
        onCardExpiryChange = { viewModel.updateCardExpiry(it) },
        onCardCvvChange = { viewModel.updateCardCvv(it) },
        onPay = {
            viewModel.proceedToNextStep()
            onComplete()
        }
    )
}

/**
 * Stateless version of PaymentScreen for better testability and previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentContent(
    uiState: BookingUiState,
    onBack: () -> Unit,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    onCardNumberChange: (String) -> Unit,
    onCardExpiryChange: (String) -> Unit,
    onCardCvvChange: (String) -> Unit,
    onPay: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.payment)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Payment Methods Section
                PaymentMethodsSection(
                    selectedMethod = uiState.selectedPaymentMethod,
                    onSelectMethod = onSelectPaymentMethod
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card Details (only if CARD is selected)
                if (uiState.selectedPaymentMethod == PaymentMethod.CARD) {
                    CardDetailsSection(
                        cardNumber = uiState.cardNumber,
                        cardNumberError = uiState.cardNumberError,
                        cardExpiry = uiState.cardExpiry,
                        cardExpiryError = uiState.cardExpiryError,
                        cardCvv = uiState.cardCvv,
                        cardCvvError = uiState.cardCvvError,
                        onCardNumberChange = onCardNumberChange,
                        onCardExpiryChange = onCardExpiryChange,
                        onCardCvvChange = onCardCvvChange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security Notice
                SecurityNotice()

                Spacer(modifier = Modifier.height(24.dp))

                // Total and Pay Button
                TotalSection(
                    totalPrice = uiState.service?.price ?: 0.0,
                    onPay = onPay,
                    enabled = uiState.canCompleteBooking
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Preview version of PaymentScreen with mock data.
 */
@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    val mockService = Service(
        id = "s1",
        providerId = "p1",
        name = "Full Body Massage",
        description = "Relaxing 60-minute massage",
        price = 80.0,
        duration = 60
    )

    val mockUiState = BookingUiState(
        currentStep = BookingStep.PAYMENT,
        service = mockService,
        selectedPaymentMethod = PaymentMethod.CARD,
        cardNumber = "4532015112830366",
        cardExpiry = "12/25",
        cardCvv = "123",
        isLoading = false,
        error = null,
    )

    PaymentContent(
        uiState = mockUiState,
        onBack = {},
        onSelectPaymentMethod = {},
        onCardNumberChange = {},
        onCardExpiryChange = {},
        onCardCvvChange = {},
        onPay = {}
    )
}
