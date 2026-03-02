package dev.pastukhov.booking.presentation.ui.screens.booking.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.PaymentMethod

@Composable
fun PaymentMethodsSection(
    selectedMethod: PaymentMethod,
    onSelectMethod: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
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

// ============== Previews ==============

@Preview(showBackground = true)
@Composable
private fun PaymentMethodsSectionPreview() {
    PaymentMethodsSection(
        selectedMethod = PaymentMethod.CARD,
        onSelectMethod = {}
    )
}
