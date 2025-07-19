package com.cocode.calendar

import CalendarConverter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import utils.Strings
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

/**
 * Displays a calendar converter box that allows switching between Jalali and Gregorian date converters.
 *
 * This composable function creates a UI element for date conversion as an overlay on top of the calendar.
 * It shows a button to toggle between Jalali to Gregorian and Gregorian to Jalali converters,
 * displays the appropriate converter based on the current state, and includes a close button.
 *
 * @return A composable that displays the calendar converter UI when showConverter is true.
 */
@Composable
fun CalendarConverterBox() {
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()
    val showJalaliToGregorianConverter by viewModel.showJalaliToGregorianConverter.collectAsState()
    val showGregorianToJalaliConverter by viewModel.showGregorianToJalaliConverter.collectAsState()

    if (showConverter) {
        // Semi-transparent overlay background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header with title, toggle button, and close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📅 Date Converter",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { viewModel.toggleJalaliToGregorianConverter() },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "⇄",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.toggleConverter() },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close converter"
                                )
                            }
                        }
                    }

                    // Conversion direction indicator
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when {
                                showJalaliToGregorianConverter -> "Jalali → Gregorian"
                                showGregorianToJalaliConverter -> "Gregorian → Jalali"
                                else -> "Error"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    DateConverter(
                        showJalaliToGregorianConverter,
                        showGregorianToJalaliConverter
                    )
                }
            }
        }
    }
}

/**
 * A composable function that creates a date converter interface.
 *
 * This function provides a user interface for converting dates between Jalali and Gregorian calendars.
 * It includes input fields for year, month, and day, a convert button, and displays the converted date.
 *
 * @param showJalaliToGregorianConverter A boolean flag indicating whether to show the Jalali to Gregorian converter.
 * @param showGregorianToJalaliConverter A boolean flag indicating whether to show the Gregorian to Jalali converter.
 *
 * @return This function doesn't return a value, but creates and displays a Composable UI for date conversion.
 */
@Composable
fun DateConverter(
    showJalaliToGregorianConverter: Boolean,
    showGregorianToJalaliConverter: Boolean
) {
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var convertedDate by remember { mutableStateOf<Any?>(null) }
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()

    if (showConverter) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Input section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (showJalaliToGregorianConverter) "Enter Jalali Date"
                        else if (showGregorianToJalaliConverter) "Enter Gregorian Date"
                        else "Something is wrong",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )

                    val focusManager = LocalFocusManager.current

                    fun convertDate() {
                        convertedDate = try {
                            val y = year.toInt()
                            val m = month.toInt()
                            val d = day.toInt()
                            if (showJalaliToGregorianConverter) {
                                CalendarConverter.jalaliToGregorian(y, m, d)
                            } else if (showGregorianToJalaliConverter) {
                                CalendarConverter.gregorianToJalali(LocalDate.of(y, m, d))
                            } else {
                                throw IllegalArgumentException("Invalid conversion type")
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }

                    DateInputFields(
                        year, month, day,
                        onYearChange = { newYear ->
                            year = newYear
                            if (newYear.length == 4) {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        },
                        onMonthChange = { newMonth ->
                            month = newMonth
                            val monthNum = newMonth.toIntOrNull()
                            if (monthNum != null && monthNum in 1..12) {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        },
                        onDayChange = { day = it },
                        onYearDone = { focusManager.moveFocus(FocusDirection.Next) },
                        onMonthDone = { focusManager.moveFocus(FocusDirection.Next) },
                        onDayDone = { convertDate() }
                    )
                }
            }

            // Result section
            DisplayConvertedDate(convertedDate, year, month, day)

            if (convertedDate != null) {
                DisplayPeriodToNow(convertedDate, year, month, day)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DateInputFields(
    year: String,
    month: String,
    day: String,
    onYearChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    onYearDone: () -> Unit,
    onMonthDone: () -> Unit,
    onDayDone: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val yearFocusRequester = remember { FocusRequester() }

    // Auto-focus the Year field when the component is first displayed
    LaunchedEffect(Unit) {
        yearFocusRequester.requestFocus()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            Triple("Year", year) { input: String ->
                if (input.isEmpty() || input.length <= 4 && input.all { it.isDigit() }) {
                    onYearChange(input)
                }
            },
            Triple("Mo", month) { input: String ->
                val num = input.toIntOrNull()
                if (input.isEmpty() || input.length <= 2 && num != null && num in 1..12) {
                    onMonthChange(input)
                }
            },
            Triple("Day", day) { input: String ->
                val num = input.toIntOrNull()
                if (input.isEmpty() || input.length <= 2 && num != null && num in 1..31) {
                    onDayChange(input)
                }
            }
        ).forEachIndexed { index, (label, value, onValueChange) ->
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == 2) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        when (index) {
                            0 -> {
                                onYearDone()
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                            1 -> {
                                onMonthDone()
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        }
                    },
                    onDone = {
                        keyboardController?.hide()
                        if (index == 2) {
                            onDayDone()
                            focusManager.clearFocus()
                        }
                    }
                ),
                modifier = Modifier
                    .weight(if (index == 0) 1.5f else 1f)
                    .let { if (index == 0) it.focusRequester(yearFocusRequester) else it },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

/**
 * Displays the converted date or an error message in a Text composable.
 *
 * This function takes a converted date and the input values, and displays the converted date in a formatted string
 * or shows an error message if the conversion was unsuccessful (only when input values are provided).
 *
 * @param convertedDate The result of the date conversion, which can be either a [LocalDate]
 *                      for Gregorian dates or a [CalendarConverter.Companion.JalaliDate] for
 *                      Jalali dates. If null, an error message will be displayed only if input is provided.
 * @param year The year input value
 * @param month The month input value
 * @param day The day input value
 *
 * @return A composable [Text] element displaying either the converted date, an error message, or nothing.
 *         The function doesn't explicitly return a value, but rather emits composable content.
 */
@Composable
fun DisplayConvertedDate(convertedDate: Any?, year: String, month: String, day: String) {
    val viewModel: CalendarViewModel = viewModel()
    val showGregorianToJalaliConverter by viewModel.showGregorianToJalaliConverter.collectAsState()
    val showJalaliToGregorianConverter by viewModel.showJalaliToGregorianConverter.collectAsState()

    // Check if any input has been provided
    val hasInput = year.isNotEmpty() || month.isNotEmpty() || day.isNotEmpty()

    // Only show the result section if there's a converted date or if there's input but conversion failed
    if (convertedDate != null || hasInput) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (convertedDate != null) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                convertedDate?.let {
                    val dateString = when (it) {
                        is LocalDate -> it.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        is CalendarConverter.Companion.JalaliDate -> "${it.year}/${it.monthValue}/${it.dayOfMonth}"
                        else -> return@let
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (showJalaliToGregorianConverter) "Gregorian Date"
                            else if (showGregorianToJalaliConverter) "Jalali Date"
                            else "Result",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                } ?: run {
                    // Only show invalid date message if there's actual input
                    if (hasInput) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Invalid Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Enter a valid ${
                                    if (showJalaliToGregorianConverter) "Jalali"
                                    else if (showGregorianToJalaliConverter) "Gregorian"
                                    else "date"
                                } date",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayPeriodToNow(convertedDate: Any?, fromYear: String, fromMonth: String, fromDay: String) {

    Log.d("Calendar", "convertedDate: $convertedDate")
    if (convertedDate == null) return

    val viewModel: CalendarViewModel = viewModel()
    val showGregorianToJalaliConverter by viewModel.showGregorianToJalaliConverter.collectAsState()
    val showJalaliToGregorianConverter by viewModel.showJalaliToGregorianConverter.collectAsState()

    // Determine the date to use
    val date: LocalDate? = when {
        showJalaliToGregorianConverter -> {
            // The convertedDate is already Gregorian (LocalDate)
            convertedDate as? LocalDate
        }
        showGregorianToJalaliConverter -> {
            // The fromYear, fromMonth, and fromDay are Gregorian and need conversion to LocalDate
            val year = fromYear.toIntOrNull()
            val month = fromMonth.toIntOrNull()
            val day = fromDay.toIntOrNull()
            if (year != null && month != null && day != null) {
                try {
                    LocalDate.of(year, month, day)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
        else -> null
    }

    // Display period regardless of past or future date
    date?.let { validDate ->
        val now = LocalDate.now()
        val isFuture = validDate.isAfter(now)
        val period = if (isFuture) Period.between(now, validDate) else Period.between(validDate, now)
        val years = period.years
        val months = period.months
        val days = period.days

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⏰ ${if (isFuture) "Time Until Date" else "Time Since Date"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = buildString {
                        if (years > 0) append("$years year${if (years > 1) "s" else ""} ")
                        if (months > 0) append("$months month${if (months > 1) "s" else ""} ")
                        if (days > 0) append("$days day${if (days > 1) "s" else ""}")
                        if (isEmpty()) append("Same day")
                    }.trim(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    } ?: run {
        // Handle invalid or missing date
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Invalid or missing date",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
