package dev.pastukhov.booking.presentation.ui.screens.booking.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.domain.model.PaymentMethod

@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (method) {
        PaymentMethod.CARD -> Icons.Default.CreditCard
        PaymentMethod.CASH -> Icons.Default.Payment
        PaymentMethod.GOOGLE_PAY -> Icons.Default.CreditCard // Placeholder
    }

    Card(
        modifier = modifier
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

// ============== Previews ==============

@Preview(showBackground = true)
@Composable
private fun PaymentMethodItemCardSelectedPreview() {
    PaymentMethodItem(
        method = PaymentMethod.CARD,
        isSelected = true,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun PaymentMethodItemCashUnselectedPreview() {
    PaymentMethodItem(
        method = PaymentMethod.CASH,
        isSelected = false,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun PaymentMethodItemGooglePayUnselectedPreview() {
    PaymentMethodItem(
        method = PaymentMethod.GOOGLE_PAY,
        isSelected = false,
        onClick = {}
    )
}
