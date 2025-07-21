package com.cocode.calendar.converter

import CalendarConverter
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalendarViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

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
