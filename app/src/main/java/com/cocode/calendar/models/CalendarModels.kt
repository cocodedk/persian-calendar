package com.cocode.calendar.models

/**
 * Data class to hold the Jalali month name and the Jalali date.
 */
data class JalaliDate(val year: Int, val monthValue: Int, val dayOfMonth: Int)

/**
 * Data class representing a Jalali month with its name, numeric value, and year.
 */
data class JalaliMonth(val monthName: String, val monthValue: Int, val year: Int)

/**
 * Calendar conversion constants used across different calendar systems.
 */
object CalendarConstants {
    /**
     * Cumulative days in Gregorian months (non-leap year).
     * Index 0 = 0 days, Index 1 = 31 days (January), etc.
     */
    val gregorianMonthDays = arrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

    /**
     * Number of days in each Gregorian month (non-leap year).
     */
    val gregorianDaysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    /**
     * Number of days in each Jalali month.
     */
    val jalaliDaysInMonth = arrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
}
