package dev.pastukhov.booking.presentation.ui.screens.booking

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.model.TimeSlot
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Screen 1: Select Date and Time
 * Allows user to choose a date (calendar) and time slot.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDateTimeScreen(
    providerId: String,
    serviceId: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Initialize booking when screen loads
    LaunchedEffect(providerId, serviceId) {
        viewModel.initializeBooking(providerId, serviceId)
    }

    SelectDateTimeScreenContent(
        uiState = uiState,
        showDatePicker = showDatePicker,
        onShowDatePickerChange = { showDatePicker = it },
        onBack = onBack,
        onNext = onNext,
        onDateSelected = { viewModel.selectDate(it) },
        onTimeSelected = { viewModel.selectTime(it) },
        onProceedToNextStep = { viewModel.proceedToNextStep() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectDateTimeScreenContent(
    uiState: BookingUiState,
    showDatePicker: Boolean,
    onShowDatePickerChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    onProceedToNextStep: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_datetime)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Service Info Card
            ServiceInfoCard(
                providerName = uiState.provider?.name.orEmpty(),
                serviceName = uiState.service?.name.orEmpty(),
                servicePrice = uiState.service?.price ?: 0.0
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Selection Section
            DateSelectionSection(
                selectedDate = uiState.selectedDate,
                onSelectDate = { onShowDatePickerChange(true) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time Selection Section
            TimeSelectionSection(
                selectedTime = uiState.selectedTime,
                timeSlots = uiState.availableTimeSlots,
                isLoading = uiState.isLoadingSlots,
                onSelectTime = onTimeSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Next Button
            Button(
                onClick = {
                    onProceedToNextStep()
                    onNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = uiState.canProceedToConfirmation
            ) {
                Text(stringResource(R.string.next))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDate
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { onShowDatePickerChange(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(date)
                        }
                        onShowDatePickerChange(false)
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDatePickerChange(false) }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Composable
private fun ServiceInfoCard(
    providerName: String,
    serviceName: String,
    servicePrice: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = providerName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = serviceName,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$$servicePrice",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DateSelectionSection(
    selectedDate: LocalDate?,
    onSelectDate: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.select_date),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectDate() },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedDate != null)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (selectedDate != null)
                            selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                        else
                            stringResource(R.string.tap_to_select_date),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selectedDate != null) FontWeight.Bold else FontWeight.Normal
                    )
                }
                if (selectedDate != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSelectionSection(
    selectedTime: LocalTime?,
    timeSlots: List<TimeSlot>,
    isLoading: Boolean,
    onSelectTime: (LocalTime) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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

@Composable
private fun TimeSlotItem(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit
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
        modifier = Modifier
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

// Previews

@Preview(showBackground = true)
@Composable
fun SelectDateTimeScreenPreview() {
    val mockProvider = Provider(
        id = "p1",
        name = "Glamour Hair Studio",
        description = "Top-notch hair styling and care.",
        category = ProviderCategory.SALON,
        address = "123 Style St, Fashion City",
        city = "Fashion City",
        rating = 4.8f,
        reviewCount = 215,
        phone = "555-0101",
        workingHours = "9:00 - 18:00"
    )

    val mockService = Service(
        id = "s1",
        providerId = "p1",
        name = "Women's Haircut & Style",
        description = "A full haircut and styling session.",
        price = 75.0,
        duration = 60
    )

    val timeSlots = listOf(
        TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), isAvailable = true),
        TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0), isAvailable = true),
        TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0), isAvailable = false),
        TimeSlot(LocalTime.of(12, 0), LocalTime.of(13, 0), isAvailable = true),
        TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0), isAvailable = true),
    )

    val uiState = BookingUiState(
        provider = mockProvider,
        service = mockService,
        selectedDate = LocalDate.now(),
        selectedTime = LocalTime.of(10, 0),
        availableTimeSlots = timeSlots,
     )

    SelectDateTimeScreenContent(
        uiState = uiState,
        showDatePicker = false,
        onShowDatePickerChange = {},
        onBack = {},
        onNext = {},
        onDateSelected = {},
        onTimeSelected = {},
        onProceedToNextStep = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ServiceInfoCardPreview() {
    ServiceInfoCard(
        providerName = "Glamour Hair Studio",
        serviceName = "Women's Haircut & Style",
        servicePrice = 75.0
    )
}

@Preview(name = "Date Not Selected", showBackground = true)
@Composable
private fun DateSelectionSectionPreview_NotSelected() {
    DateSelectionSection(
        selectedDate = null,
        onSelectDate = {}
    )
}

@Preview(name = "Date Selected", showBackground = true)
@Composable
private fun DateSelectionSectionPreview_Selected() {
    DateSelectionSection(
        selectedDate = LocalDate.now(),
        onSelectDate = {}
    )
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