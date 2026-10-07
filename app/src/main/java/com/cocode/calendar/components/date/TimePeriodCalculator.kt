package com.cocode.calendar.components.date

import android.content.res.Resources
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.R
import java.time.LocalDate
import java.time.Period

/**
 * Utility functions for calculating and formatting time periods between dates.
 */
object TimePeriodCalculator {

    /**
     * Calculates the time period between two dates and formats it as a human-readable string.
     * @param resources Used to look up the words and plurals
     * @param fromDate The starting date
     * @param toDate The ending date
     * @return Formatted period string (e.g., "2 years 3 months 5 days")
     */
    fun calculateAndFormatPeriod(resources: Resources, fromDate: LocalDate, toDate: LocalDate): String {
        val isFuture = toDate.isAfter(fromDate)
        val period = if (isFuture) Period.between(fromDate, toDate) else Period.between(toDate, fromDate)
        val years = period.years
        val months = period.months
        val days = period.days

        // The count goes in as text, so digits stay Latin whatever the phone's language is.
        return buildList {
            if (years > 0) add(resources.getQuantityString(R.plurals.period_years, years, years.toString()))
            if (months > 0) add(resources.getQuantityString(R.plurals.period_months, months, months.toString()))
            if (days > 0) add(resources.getQuantityString(R.plurals.period_days, days, days.toString()))
        }.joinToString(" ").ifEmpty { resources.getString(R.string.converter_period_same_day) }
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
     * @param resources Used to look up the label text
     * @param date The date being compared to now
     * @return The label text (e.g., "Time until this date" or "Time since this date")
     */
    fun getPeriodLabel(resources: Resources, date: LocalDate): String {
        val now = LocalDate.now()
        val isFuture = date.isAfter(now)
        return resources.getString(
            if (isFuture) R.string.converter_period_until else R.string.converter_period_since
        )
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
