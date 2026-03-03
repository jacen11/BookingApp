package dev.pastukhov.booking.presentation.ui.screens.bookingdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.R


/**
 * Payment status card.
 */
@Composable
fun PaymentStatusCard(
    isPaid: Boolean,
    paymentMethod: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                icon = Icons.Default.Payment,
                title = stringResource(R.string.payment_status)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isPaid) Color(0xFF4CAF50) else Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPaid) stringResource(R.string.payment_paid) else stringResource(R.string.payment_pending),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isPaid) Color(0xFF4CAF50) else Color(0xFFFFC107)
                )
            }
            if (paymentMethod.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = paymentMethod,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentStatusCardPaidPreview() {
    MaterialTheme {
        PaymentStatusCard(isPaid = true, paymentMethod = "Tarjeta")
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentStatusCardPendingPreview() {
    MaterialTheme {
        PaymentStatusCard(isPaid = false, paymentMethod = "Efectivo")
    }
}
