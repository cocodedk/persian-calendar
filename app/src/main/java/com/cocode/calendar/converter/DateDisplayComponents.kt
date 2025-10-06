package com.cocode.calendar.converter

import androidx.compose.runtime.Composable

/**
 * DateDisplayComponents.kt - Facade for date display components.
 *
 * This file now acts as a simple facade that delegates to the specialized
 * date display components for better maintainability and organization.
 *
 * The implementation has been split into:
 * - components/date/DateDisplayComponents.kt for main display components
 * - components/date/DateFormattingUtils.kt for formatting utilities
 * - components/date/TimePeriodCalculator.kt for time period calculations
 */

// Re-export display components for backward compatibility
@Composable
fun DisplayConvertedDate(convertedDate: Any?, year: String, month: String, day: String) {
    com.cocode.calendar.components.date.DisplayConvertedDate(
        convertedDate = convertedDate,
        year = year,
        month = month,
        day = day
    )
}

@Composable
fun DisplayPeriodToNow(convertedDate: Any?, fromYear: String, fromMonth: String, fromDay: String) {
    com.cocode.calendar.components.date.DisplayPeriodToNow(
        convertedDate = convertedDate,
        fromYear = fromYear,
        fromMonth = fromMonth,
        fromDay = fromDay
    )
}
