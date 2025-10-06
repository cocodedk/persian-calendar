package com.cocode.calendar.components.date

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalendarViewModel
import java.time.LocalDate
import java.time.Period

/**
 * Utility functions for calculating and formatting time periods between dates.
 */
object TimePeriodCalculator {

    /**
     * Calculates the time period between two dates and formats it as a human-readable string.
     * @param fromDate The starting date
     * @param toDate The ending date
     * @return Formatted period string (e.g., "2 years 3 months 5 days")
     */
    fun calculateAndFormatPeriod(fromDate: LocalDate, toDate: LocalDate): String {
        val isFuture = toDate.isAfter(fromDate)
        val period = if (isFuture) Period.between(fromDate, toDate) else Period.between(toDate, fromDate)
        val years = period.years
        val months = period.months
        val days = period.days

        return buildString {
            if (years > 0) append("$years year${if (years > 1) "s" else ""} ")
            if (months > 0) append("$months month${if (months > 1) "s" else ""} ")
            if (days > 0) append("$days day${if (days > 1) "s" else ""}")
            if (isEmpty()) append("Same day")
        }.trim()
    }

    /**
     * Determines the appropriate date to use for period calculation based on converter state.
     * @param convertedDate The converted date result
     * @param fromYear The year input value
     * @param fromMonth The month input value
     * @param fromDay The day input value
     * @param viewModel The CalendarViewModel to check converter states
     * @return The LocalDate to use for calculations, or null if invalid
     */
    @Composable
    fun determineCalculationDate(
        convertedDate: Any?,
        fromYear: String,
        fromMonth: String,
        fromDay: String,
        viewModel: CalendarViewModel
    ): LocalDate? {
        val showGregorianToJalaliConverter by viewModel.showGregorianToJalaliConverter.collectAsState()
        val showJalaliToGregorianConverter by viewModel.showJalaliToGregorianConverter.collectAsState()

        return when {
            showJalaliToGregorianConverter -> {
                // The convertedDate is already Gregorian (LocalDate)
                convertedDate as? LocalDate
            }
            showGregorianToJalaliConverter -> {
                // The fromYear, fromMonth, and fromDay are Gregorian and need conversion to LocalDate
                DateFormattingUtils.validateAndCreateDate(fromYear, fromMonth, fromDay)
            }
            else -> null
        }
    }

    /**
     * Gets the appropriate label for the time period display.
     * @param date The date being compared to now
     * @return The label text (e.g., "Time Until Date" or "Time Since Date")
     */
    fun getPeriodLabel(date: LocalDate): String {
        val now = LocalDate.now()
        val isFuture = date.isAfter(now)
        return "⏰ ${if (isFuture) "Time Until Date" else "Time Since Date"}"
    }

    /**
     * Checks if a date is in the future compared to today.
     * @param date The date to check
     * @return true if the date is in the future
     */
    fun isFutureDate(date: LocalDate): Boolean {
        return date.isAfter(LocalDate.now())
    }
}
