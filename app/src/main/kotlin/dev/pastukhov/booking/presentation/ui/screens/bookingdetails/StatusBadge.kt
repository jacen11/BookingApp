package dev.pastukhov.booking.presentation.ui.screens.bookingdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
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
import dev.pastukhov.booking.domain.model.BookingStatus

/**
 * Status badge with color coding.
 */
@Composable
fun StatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, icon, textRes) = when (status) {
        BookingStatus.PENDING, BookingStatus.CONFIRMED -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.1f),
            Icons.Default.Schedule,
            R.string.booking_status_active
        )

        BookingStatus.COMPLETED -> Triple(
            Color(0xFF9E9E9E).copy(alpha = 0.1f),
            Icons.Default.CheckCircle,
            R.string.booking_status_completed
        )

        BookingStatus.CANCELLED, BookingStatus.NO_SHOW -> Triple(
            Color(0xFFF44336).copy(alpha = 0.1f),
            Icons.Default.Close,
            R.string.booking_status_cancelled
        )
    }

    val textColor = when (status) {
        BookingStatus.PENDING, BookingStatus.CONFIRMED -> Color(0xFF4CAF50)
        BookingStatus.COMPLETED -> Color(0xFF9E9E9E)
        BookingStatus.CANCELLED, BookingStatus.NO_SHOW -> Color(0xFFF44336)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(textRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatusBadgePendingPreview() {
    MaterialTheme {
        StatusBadge(status = BookingStatus.PENDING)
    }
}

@Preview(showBackground = true)
@Composable
fun StatusBadgeConfirmedPreview() {
    MaterialTheme {
        StatusBadge(status = BookingStatus.CONFIRMED)
    }
}

@Preview(showBackground = true)
@Composable
fun StatusBadgeCompletedPreview() {
    MaterialTheme {
        StatusBadge(status = BookingStatus.COMPLETED)
    }
}

@Preview(showBackground = true)
@Composable
fun StatusBadgeCancelledPreview() {
    MaterialTheme {
        StatusBadge(status = BookingStatus.CANCELLED)
    }
}
