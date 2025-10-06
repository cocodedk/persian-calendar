package com.cocode.calendar.utils

import com.cocode.calendar.models.CalendarConstants
import com.cocode.calendar.models.JalaliDate

/**
 * Utility class for Jalali calendar week calculations.
 */
object JalaliWeekCalculator {

    /**
     * Returns the week number for a given Jalali date.
     *
     * @param jalaliDate The JalaliDate object
     * @return An integer representing the week number
     */
    fun getJalaliWeekNumber(jalaliDate: JalaliDate): Int {
        // Calculate the number of days passed since the start of the Jalali year
        val daysPassed = CalendarConstants.jalaliDaysInMonth.take(jalaliDate.monthValue - 1).sum() + jalaliDate.dayOfMonth

        // Calculate the week number
        val weekNumber = (daysPassed + 6) / 7

        return weekNumber
    }
}
