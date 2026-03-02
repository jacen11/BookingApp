package dev.pastukhov.booking.presentation.ui.screens.booking.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.TimeSlot
import java.time.LocalTime

@Composable
fun TimeSelectionSection(
    selectedTime: LocalTime?,
    timeSlots: List<TimeSlot>,
    isLoading: Boolean,
    onSelectTime: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.select_time),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (timeSlots.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.select_date_first),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(timeSlots) { slot ->
                    TimeSlotItem(
                        timeSlot = slot,
                        isSelected = selectedTime == slot.startTime,
                        onClick = {
                            if (slot.isAvailable) {
                                onSelectTime(slot.startTime)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(name = "Time Loading", showBackground = true)
@Composable
private fun TimeSelectionSectionPreview_Loading() {
    TimeSelectionSection(
        selectedTime = null,
        timeSlots = emptyList(),
        isLoading = true,
        onSelectTime = {}
    )
}

@Preview(name = "Time No Slots", showBackground = true)
@Composable
private fun TimeSelectionSectionPreview_NoSlots() {
    TimeSelectionSection(
        selectedTime = null,
        timeSlots = emptyList(),
        isLoading = false,
        onSelectTime = {}
    )
}

@Preview(name = "Time With Slots", showBackground = true)
@Composable
private fun TimeSelectionSectionPreview_WithSlots() {
    val timeSlots = listOf(
        TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), isAvailable = true),
        TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0), isAvailable = true),
        TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0), isAvailable = false),
    )
    TimeSelectionSection(
        selectedTime = LocalTime.of(10, 0),
        timeSlots = timeSlots,
        isLoading = false,
        onSelectTime = {}
    )
}
