package com.cocode.calendar.converter

/**
 * The Jalali calendar's year and month lengths, from the same day count that
 * [JalaliToGregorianConverter] uses, so a date is valid here exactly when it has its own day
 * there. Pure Kotlin, no Android classes.
 */
object JalaliCalendar {

    /** Days from the start of Jalali year 979 to the first day of [year]. */
    fun daysBeforeYear(year: Int): Int {
        val y = year - 979
        return 365 * y + (y / 33) * 8 + ((y % 33 + 3) / 4)
    }

    /** True when Esfand of [year] has 30 days. 1399, 1403 and 1408 are leap; 1402 and 1404 are not. */
    fun isLeapYear(year: Int): Boolean = daysBeforeYear(year + 1) - daysBeforeYear(year) == 366

    /** The number of days in [month] (1-12) of [year]: 31 for months 1-6, 30 for 7-11, 29 or 30 for Esfand. */
    fun daysInMonth(year: Int, month: Int): Int = when (month) {
        in 1..6 -> 31
        in 7..11 -> 30
        12 -> if (isLeapYear(year)) 30 else 29
        else -> 0
    }

    /** True when [day] exists in [month] of [year]. */
    fun isValidDate(year: Int, month: Int, day: Int): Boolean =
        day in 1..daysInMonth(year, month)
}
