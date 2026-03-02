package dev.pastukhov.booking.presentation.ui.screens.booking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.domain.model.Service
import dev.pastukhov.booking.domain.model.TimeSlot
import dev.pastukhov.booking.presentation.ui.screens.booking.component.DateSelectionSection
import dev.pastukhov.booking.presentation.ui.screens.booking.component.ServiceInfoCard
import dev.pastukhov.booking.presentation.ui.screens.booking.component.TimeSelectionSection
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

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