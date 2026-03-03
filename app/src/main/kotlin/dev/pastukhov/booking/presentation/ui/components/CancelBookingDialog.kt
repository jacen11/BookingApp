package dev.pastukhov.booking.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.R
import dev.pastukhov.booking.ui.theme.BookingAppTheme


/**
 * Cancel booking confirmation dialog.
 */
@Composable
fun CancelBookingDialog(
    isCancelling: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isCancelling) onDismiss() },
        title = {
            Text(
                text = stringResource(R.string.cancel_booking_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(stringResource(R.string.cancel_booking_message))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isCancelling
            ) {
                if (isCancelling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.yes_cancel))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isCancelling
            ) {
                Text(stringResource(R.string.no_keep))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CancelBookingDialogPreview() {
    BookingAppTheme {
        CancelBookingDialog(
            isCancelling = false,
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CancelBookingDialogCancellingPreview() {
    BookingAppTheme {
        CancelBookingDialog(
            isCancelling = true,
            onConfirm = {},
            onDismiss = {}
        )
    }
}
