package com.cocode.calendar.components.date

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.R
import com.cocode.calendar.models.JalaliDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Utility functions for date formatting and display logic.
 */
object DateFormattingUtils {

    /**
     * Formats a date for display based on its type.
     * @param date The date to format (LocalDate or JalaliDate)
     * @return Formatted date string
     */
    fun formatDateForDisplay(date: Any): String? {
        return when (date) {
            is LocalDate -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            is JalaliDate -> "${date.year}/${date.monthValue}/${date.dayOfMonth}"
            else -> null
        }
    }

    /**
     * Gets the appropriate label text for the conversion result based on the active converter.
     * @param viewModel The CalendarViewModel to check converter states
     * @return The label text to display
     */
    @Composable
    fun getConversionResultLabel(viewModel: CalendarViewModel): String {
        val showJalaliToGregorianConverter by viewModel.showJalaliToGregorianConverter.collectAsState()
        val showGregorianToJalaliConverter by viewModel.showGregorianToJalaliConverter.collectAsState()

        return stringResource(
            when {
                showJalaliToGregorianConverter -> R.string.converter_result_gregorian
                showGregorianToJalaliConverter -> R.string.converter_result_jalali
                else -> R.string.converter_result
            }
        )
    }

    /**
     * Gets the message that asks for a valid date, naming the calendar the user is typing in.
     * @param viewModel The CalendarViewModel to check converter states
     * @return The message to show under the "Invalid Date" title
     */
    @Composable
    fun getInvalidDateMessage(viewModel: CalendarViewModel): String {
        val showJalaliToGregorianConverter by viewModel.showJalaliToGregorianConverter.collectAsState()
        val showGregorianToJalaliConverter by viewModel.showGregorianToJalaliConverter.collectAsState()

        return stringResource(
            when {
                showJalaliToGregorianConverter -> R.string.converter_invalid_jalali
                showGregorianToJalaliConverter -> R.string.converter_invalid_gregorian
                else -> R.string.converter_invalid_other
            }
        )
    }

    /**
     * Checks if any input fields have content.
     * @param year The year input value
     * @param month The month input value
     * @param day The day input value
     * @return true if every field has content
     */
    fun hasCompleteInput(year: String, month: String, day: String): Boolean {
        return year.isNotEmpty() && month.isNotEmpty() && day.isNotEmpty()
    }

    /**
     * Validates if the given year, month, day form a valid date.
     * @param year The year value
     * @param month The month value
     * @param day The day value
     * @return The LocalDate if valid, null otherwise
     */
    fun validateAndCreateDate(year: String, month: String, day: String): LocalDate? {
        val yearInt = year.toIntOrNull()
        val monthInt = month.toIntOrNull()
        val dayInt = day.toIntOrNull()

        return if (yearInt != null && monthInt != null && dayInt != null) {
            try {
                LocalDate.of(yearInt, monthInt, dayInt)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
}
