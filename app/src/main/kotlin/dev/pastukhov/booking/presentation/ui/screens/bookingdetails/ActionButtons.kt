package dev.pastukhov.booking.presentation.ui.screens.bookingdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.R

/**
 * Action buttons based on booking status.
 */
@Composable
fun ActionButtons(
    isActive: Boolean,
    isCompleted: Boolean,
    isCancelled: Boolean,
    onCancelClick: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onRateClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            // Active booking - show cancel, call, WhatsApp
            isActive -> {
                // Cancel button (red)
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.cancel_booking))
                }

                // Call and WhatsApp buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCallClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.call_provider))
                    }

                    OutlinedButton(
                        onClick = onWhatsAppClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF25D366)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WhatsApp")
                    }
                }
            }

            // Completed booking - show rate and repeat
            isCompleted -> {
                Button(
                    onClick = onRateClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.rate_service))
                }

                OutlinedButton(
                    onClick = onRepeatClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.repeat_booking))
                }
            }

            // Cancelled booking - show repeat only
            isCancelled -> {
                Button(
                    onClick = onRepeatClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.repeat_booking))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionButtonsActivePreview() {
    MaterialTheme {
        ActionButtons(
            isActive = true,
            isCompleted = false,
            isCancelled = false,
            onCancelClick = {},
            onCallClick = {},
            onWhatsAppClick = {},
            onRepeatClick = {},
            onRateClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActionButtonsCompletedPreview() {
    MaterialTheme {
        ActionButtons(
            isActive = false,
            isCompleted = true,
            isCancelled = false,
            onCancelClick = {},
            onCallClick = {},
            onWhatsAppClick = {},
            onRepeatClick = {},
            onRateClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActionButtonsCancelledPreview() {
    MaterialTheme {
        ActionButtons(
            isActive = false,
            isCompleted = false,
            isCancelled = true,
            onCancelClick = {},
            onCallClick = {},
            onWhatsAppClick = {},
            onRepeatClick = {},
            onRateClick = {}
        )
    }
}