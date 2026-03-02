package dev.pastukhov.booking.presentation.ui.screens.booking.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.TimeSlot
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimeSlotItem(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        !timeSlot.isAvailable -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !timeSlot.isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (!timeSlot.isAvailable) {
                    Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(enabled = timeSlot.isAvailable) { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeSlot.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (!timeSlot.isAvailable) {
                Text(
                    text = stringResource(R.string.unavailable),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(name = "Time Slot Available")
@Composable
private fun TimeSlotItemPreview_Available() {
    TimeSlotItem(
        timeSlot = TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), true),
        isSelected = false,
        onClick = {}
    )
}

@Preview(name = "Time Slot Selected")
@Composable
private fun TimeSlotItemPreview_Selected() {
    TimeSlotItem(
        timeSlot = TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0), true),
        isSelected = true,
        onClick = {}
    )
}

@Preview(name = "Time Slot Unavailable")
@Composable
private fun TimeSlotItemPreview_Unavailable() {
    TimeSlotItem(
        timeSlot = TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0), false),
        isSelected = false,
        onClick = {}
    )
}
