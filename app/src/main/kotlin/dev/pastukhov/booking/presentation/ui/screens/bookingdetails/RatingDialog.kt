package dev.pastukhov.booking.presentation.ui.screens.bookingdetails

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import dev.pastukhov.booking.R

/**
 * Rating dialog (placeholder implementation).
 */
@Composable
fun RatingDialog(
    isRating: Boolean,
    onSubmit: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isRating) onDismiss() },
        title = {
            Text(
                text = stringResource(R.string.rate_service),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Rating functionality coming soon!")
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(5, "") },
                enabled = !isRating
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isRating
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RatingDialogPreview() {
    MaterialTheme {
        RatingDialog(
            isRating = false,
            onSubmit = { _, _ -> },
            onDismiss = {}
        )
    }
}