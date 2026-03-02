package dev.pastukhov.booking.presentation.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.PaymentMethod

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
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.payment)) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.goBack()
                        onBack()
                    }) {
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
                    onSelectMethod = { viewModel.selectPaymentMethod(it) }
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
                        onCardNumberChange = { viewModel.updateCardNumber(it) },
                        onCardExpiryChange = { viewModel.updateCardExpiry(it) },
                        onCardCvvChange = { viewModel.updateCardCvv(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security Notice
                SecurityNotice()

                Spacer(modifier = Modifier.height(24.dp))

                // Total and Pay Button
                TotalSection(
                    totalPrice = uiState.service?.price ?: 0.0,
                    onPay = {
                        viewModel.proceedToNextStep()
                        onComplete()
                    },
                    enabled = uiState.canCompleteBooking
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PaymentMethodsSection(
    selectedMethod: PaymentMethod,
    onSelectMethod: (PaymentMethod) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.payment_method),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        PaymentMethod.entries.forEach { method ->
            PaymentMethodItem(
                method = method,
                isSelected = selectedMethod == method,
                onClick = { onSelectMethod(method) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (method) {
        PaymentMethod.CARD -> Icons.Default.CreditCard
        PaymentMethod.CASH -> Icons.Default.Payment
        PaymentMethod.GOOGLE_PAY -> Icons.Default.CreditCard // Placeholder
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = method.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun CardDetailsSection(
    cardNumber: String,
    cardNumberError: String?,
    cardExpiry: String,
    cardExpiryError: String?,
    cardCvv: String,
    cardCvvError: String?,
    onCardNumberChange: (String) -> Unit,
    onCardExpiryChange: (String) -> Unit,
    onCardCvvChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.card_details),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = cardNumber,
            onValueChange = onCardNumberChange,
            label = { Text(stringResource(R.string.card_number)) },
            placeholder = { Text("1234 5678 9012 3456") },
            isError = cardNumberError != null,
            supportingText = cardNumberError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = cardExpiry,
                onValueChange = onCardExpiryChange,
                label = { Text(stringResource(R.string.expiry)) },
                placeholder = { Text("MM/YY") },
                isError = cardExpiryError != null,
                supportingText = cardExpiryError?.let { { Text(it) } },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = cardCvv,
                onValueChange = onCardCvvChange,
                label = { Text(stringResource(R.string.cvv)) },
                placeholder = { Text("123") },
                isError = cardCvvError != null,
                supportingText = cardCvvError?.let { { Text(it) } },
                modifier = Modifier.weight(1f),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
        }
    }
}

@Composable
private fun SecurityNotice() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.security_notice),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TotalSection(
    totalPrice: Double,
    onPay: () -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.total),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$$totalPrice",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onPay,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        ) {
            Text(
                text = stringResource(R.string.pay_and_book),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
