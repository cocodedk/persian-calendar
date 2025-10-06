package com.cocode.calendar.components.date

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
import com.cocode.calendar.models.JalaliDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Displays the converted date or an error message in a Text composable.
 *
 * This function takes a converted date and the input values, and displays the converted date in a formatted string
 * or shows an error message if the conversion was unsuccessful (only when input values are provided).
 *
 * @param convertedDate The result of the date conversion, which can be either a [LocalDate]
 *                      for Gregorian dates or a [JalaliDate] for
 *                      Jalali dates. If null, an error message will be displayed only if input is provided.
 * @param year The year input value
 * @param month The month input value
 * @param day The day input value
 */
@Composable
fun DisplayConvertedDate(convertedDate: Any?, year: String, month: String, day: String) {
    val viewModel: CalendarViewModel = viewModel()

    // Check if any input has been provided
    val hasInput = DateFormattingUtils.hasAnyInput(year, month, day)

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
                    val dateString = DateFormattingUtils.formatDateForDisplay(it) ?: return@let
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = DateFormattingUtils.getConversionResultLabel(viewModel),
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
                                text = "Enter a valid ${DateFormattingUtils.getCalendarTypeForError(viewModel)} date",
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

/**
 * Displays the time period from a given date to now.
 *
 * This function calculates and displays the time elapsed between a given date and the current date.
 * It handles both past and future dates appropriately.
 *
 * @param convertedDate The converted date to calculate period from
 * @param fromYear The year input value
 * @param fromMonth The month input value
 * @param fromDay The day input value
 */
@Composable
fun DisplayPeriodToNow(convertedDate: Any?, fromYear: String, fromMonth: String, fromDay: String) {

    if (convertedDate == null) return

    val viewModel: CalendarViewModel = viewModel()

    // Determine the date to use
    val date = TimePeriodCalculator.determineCalculationDate(
        convertedDate, fromYear, fromMonth, fromDay, viewModel
    )

    // Display period regardless of past or future date
    date?.let { validDate ->
        val now = LocalDate.now()
        val isFuture = TimePeriodCalculator.isFutureDate(validDate)
        val periodText = TimePeriodCalculator.calculateAndFormatPeriod(
            if (isFuture) now else validDate,
            if (isFuture) validDate else now
        )

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
                    text = TimePeriodCalculator.getPeriodLabel(validDate),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = periodText,
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
