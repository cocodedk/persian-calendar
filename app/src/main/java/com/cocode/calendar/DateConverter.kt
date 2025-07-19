package com.cocode.calendar

import CalendarConverter
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
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
 * This composable function creates a UI element for date conversion. It shows a button to toggle
 * between Jalali to Gregorian and Gregorian to Jalali converters, and displays the appropriate
 * converter based on the current state.
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
        Column {
            Button(
                onClick = { viewModel.toggleJalaliToGregorianConverter() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = when {
                        showJalaliToGregorianConverter -> Strings.Converter.SWITCH_TO_GREGORIAN_TO_JALALI
                        showGregorianToJalaliConverter -> Strings.Converter.SWITCH_TO_JALALI_TO_GREGORIAN
                        else -> Strings.Error.WRONG
                    },
                    color = Color.White
                )
            }

            DateConverter(
                showJalaliToGregorianConverter,
                showGregorianToJalaliConverter
            )
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                if (showJalaliToGregorianConverter) Strings.Converter.ENTER_JALALI_DATE
                else if (showGregorianToJalaliConverter) Strings.Converter.ENTER_GREGORIAN_DATE
                else "Something is wrong",
                //style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

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
                onYearChange = { year = it },
                onMonthChange = { month = it },
                onDayChange = { day = it },
                onYearDone = { focusManager.moveFocus(FocusDirection.Next) },
                onMonthDone = { focusManager.moveFocus(FocusDirection.Next) },
                onDayDone = { convertDate() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DisplayConvertedDate(convertedDate)

            if( convertedDate != null) {
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf(
            Triple(Strings.Converter.YEAR, year) { input: String ->
                if (input.isEmpty() || input.length <= 4 && input.all { it.isDigit() }) {
                    onYearChange(input)
                }
            },
            Triple(Strings.Converter.MONTH, month) { input: String ->
                val num = input.toIntOrNull()
                if (input.isEmpty() || input.length <= 2 && num != null && num in 1..12) {
                    onMonthChange(input)
                }
            },
            Triple(Strings.Converter.DAY, day) { input: String ->
                val num = input.toIntOrNull()
                if (input.isEmpty() || input.length <= 2 && num != null && num in 1..31) {
                    onDayChange(input)
                }
            }
        ).forEachIndexed { index, (label, value, onValueChange) ->
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
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
                        keyboardController?.hide() // Close the keyboard
                        if (index == 2) {
                            onDayDone()
                            focusManager.clearFocus()
                        }
                    }
                ),

                modifier = Modifier
                    .width(100.dp) // Adjust this size for "Year"
                    .then(if (index > 0) Modifier.width(40.dp) else Modifier), // Adjust sizes for "Month" and "Day"
                singleLine = true
            )
            if (index < 2) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

/**
 * Displays the converted date or an error message in a Text composable.
 *
 * This function takes a converted date and a boolean flag indicating the direction of conversion
 * (Jalali to Gregorian or vice versa). It then displays the converted date in a formatted string
 * or shows an error message if the conversion was unsuccessful.
 *
 * @param convertedDate The result of the date conversion, which can be either a [LocalDate]
 *                      for Gregorian dates or a [CalendarConverter.Companion.JalaliDate] for
 *                      Jalali dates. If null, an error message will be displayed.
 *
 * @return A composable [Text] element displaying either the converted date or an error message.
 *         The function doesn't explicitly return a value, but rather emits composable content.
 */
@Composable
fun DisplayConvertedDate(convertedDate: Any?) {
    val viewModel: CalendarViewModel = viewModel()
    val showGregorianToJalaliConverter by viewModel.showGregorianToJalaliConverter.collectAsState()
    val showJalaliToGregorianConverter by viewModel.showJalaliToGregorianConverter.collectAsState()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        convertedDate?.let {
            val dateString = when (it) {
                is LocalDate -> it.format(DateTimeFormatter.ISO_LOCAL_DATE)
                is CalendarConverter.Companion.JalaliDate -> "${it.year}/${it.monthValue}/${it.dayOfMonth}"
                else -> return@let
            }
            Text(
                "${
                    if (showJalaliToGregorianConverter) Strings.Calendar.GREGORIAN
                    else if (showGregorianToJalaliConverter) Strings.Calendar.JALALI
                    else "something is wrong"
                } Date: $dateString",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        } ?: run {
            Text(
                "Enter a Valid ${
                    if (showJalaliToGregorianConverter) Strings.Calendar.JALALI
                    else if (showGregorianToJalaliConverter) Strings.Calendar.GREGORIAN
                    else "something is wrong"
                } Date",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Yellow,
                textAlign = TextAlign.Center
            )
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                buildString {
                    if (isFuture) append("In ") else append("Since ")
                    if (years > 0) append("$years year${if (years > 1) "s" else ""} ")
                    if (months > 0) append("$months month${if (months > 1) "s" else ""} ")
                    if (days > 0) append("$days day${if (days > 1) "s" else ""}")
                }.trim(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    } ?: run {
        // Handle invalid or missing date
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Invalid or missing date",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red
            )
        }
    }
}
