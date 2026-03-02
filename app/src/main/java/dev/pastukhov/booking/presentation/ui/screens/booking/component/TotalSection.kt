package dev.pastukhov.booking.presentation.ui.screens.booking.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import dev.pastukhov.booking.R

@Composable
fun TotalSection(
    totalPrice: Double,
    onPay: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
                text = "$totalPrice",
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

// ============== Previews ==============

@Preview(showBackground = true)
@Composable
private fun TotalSectionPreview() {
    TotalSection(
        totalPrice = 150.0,
        onPay = {},
        enabled = true
    )
}

@Preview(showBackground = true)
@Composable
private fun TotalSectionDisabledPreview() {
    TotalSection(
        totalPrice = 150.0,
        onPay = {},
        enabled = false
    )
}
