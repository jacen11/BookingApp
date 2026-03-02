package dev.pastukhov.booking.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Booking
import dev.pastukhov.booking.domain.model.BookingStatus
import dev.pastukhov.booking.presentation.viewmodel.BookingTab
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Booking item card component.
 * Displays booking information with action buttons based on status.
 */
@Composable
fun BookingItemCard(
    booking: Booking,
    tab: BookingTab,
    onCancelClick: () -> Unit,
    onCallClick: (String) -> Unit,
    onMessageClick: (String) -> Unit,
    onRepeatClick: (Booking) -> Unit,
    onRateClick: (Booking) -> Unit,
    providerPhone: String = "+52 55 1234 5678",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Service name and Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Provider name
            Text(
                text = booking.providerName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date and Time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateTimeInfo(
                    date = booking.date.format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
                    ),
                    time = booking.time.format(DateTimeFormatter.ofPattern("HH:mm"))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatPrice(booking.totalPrice),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons based on tab
            ActionButtons(
                tab = tab,
                onCancelClick = onCancelClick,
                onCallClick = { onCallClick(providerPhone) },
                onMessageClick = { onMessageClick(providerPhone) },
                onRepeatClick = { onRepeatClick(booking) },
                onRateClick = { onRateClick(booking) }
            )
        }
    }
}

/**
 * Status badge with color based on booking status.
 */
@Composable
private fun StatusBadge(status: BookingStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        BookingStatus.PENDING -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            stringResource(R.string.booking_status_pending)
        )
        BookingStatus.CONFIRMED -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            stringResource(R.string.booking_status_confirmed)
        )
        BookingStatus.COMPLETED -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFF1565C0),
            stringResource(R.string.booking_status_completed)
        )
        BookingStatus.CANCELLED -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            stringResource(R.string.booking_status_cancelled)
        )
        BookingStatus.NO_SHOW -> Triple(
            Color(0xFFECEFF1),
            Color(0xFF546E7A),
            stringResource(R.string.booking_status_no_show)
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

/**
 * Date and time display row.
 */
@Composable
private fun DateTimeInfo(
    date: String,
    time: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Action buttons based on the selected tab.
 */
@Composable
private fun ActionButtons(
    tab: BookingTab,
    onCancelClick: () -> Unit,
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onRateClick: () -> Unit
) {
    when (tab) {
        BookingTab.ACTIVE -> {
            // Active bookings: Cancel, Call, Message
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.cancel_booking))
                }
                Button(
                    onClick = onCallClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.call_provider))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onMessageClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.message_provider))
            }
        }
        BookingTab.HISTORY -> {
            // History: Repeat, Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRepeatClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.repeat_booking))
                }
                Button(
                    onClick = onRateClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.rate_service))
                }
            }
        }
        BookingTab.CANCELLED -> {
            // Cancelled: Repeat only
            Button(
                onClick = onRepeatClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.repeat_booking))
            }
        }
    }
}

/**
 * Format price with currency symbol.
 */
private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return format.format(price)
}
