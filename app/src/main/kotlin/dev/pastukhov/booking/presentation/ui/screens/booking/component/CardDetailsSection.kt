package dev.pastukhov.booking.presentation.ui.screens.booking.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.R

@Composable
fun CardDetailsSection(
    cardNumber: String,
    cardNumberError: String?,
    cardExpiry: String,
    cardExpiryError: String?,
    cardCvv: String,
    cardCvvError: String?,
    onCardNumberChange: (String) -> Unit,
    onCardExpiryChange: (String) -> Unit,
    onCardCvvChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
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

// ============== Previews ==============

@Preview(showBackground = true)
@Composable
private fun CardDetailsSectionPreview() {
    CardDetailsSection(
        cardNumber = "1234 5678 9012 3456",
        cardNumberError = null,
        cardExpiry = "12/25",
        cardExpiryError = null,
        cardCvv = "123",
        cardCvvError = null,
        onCardNumberChange = {},
        onCardExpiryChange = {},
        onCardCvvChange = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CardDetailsSectionWithErrorsPreview() {
    CardDetailsSection(
        cardNumber = "123",
        cardNumberError = "Invalid card number",
        cardExpiry = "13/25",
        cardExpiryError = "Invalid expiry date",
        cardCvv = "12",
        cardCvvError = "Invalid CVV",
        onCardNumberChange = {},
        onCardExpiryChange = {},
        onCardCvvChange = {}
    )
}
