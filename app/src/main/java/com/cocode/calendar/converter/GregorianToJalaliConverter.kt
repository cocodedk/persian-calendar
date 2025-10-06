package com.cocode.calendar.converter

import com.cocode.calendar.models.CalendarConstants
import com.cocode.calendar.models.JalaliDate
import utils.Strings
import java.time.LocalDate

/**
 * Converter for transforming Gregorian dates to Jalali dates.
 */
object GregorianToJalaliConverter {

    /**
     * Converts a Gregorian date to a Jalali date.
     *
     * @param date The Gregorian LocalDate object
     * @return A JalaliDate object representing the Jalali date
     */
    fun gregorianToJalali(date: LocalDate): JalaliDate {
        val gregorianYear = date.year
        val gregorianMonth = date.monthValue
        val gregorianDay = date.dayOfMonth

        val (jalaliYear, adjustedGregorianYear) = if (gregorianYear > 1600) {
            979 to gregorianYear - 1600
        } else {
            0 to gregorianYear - 621
        }

        val adjustedGregorianYear2 = adjustedGregorianYear + if (gregorianMonth > 2) 1 else 0
        var days = (365 * adjustedGregorianYear) +
                   ((adjustedGregorianYear2 + 3) / 4) -
                   ((adjustedGregorianYear2 + 99) / 100) +
                   ((adjustedGregorianYear2 + 399) / 400) -
                   80 + gregorianDay + CalendarConstants.gregorianMonthDays[gregorianMonth - 1]

        var finalJalaliYear = jalaliYear + 33 * (days / 12053)
        days %= 12053
        finalJalaliYear += 4 * (days / 1461)
        days %= 1461

        if (days > 365) {
            finalJalaliYear += (days - 1) / 365
            days = (days - 1) % 365
        }

        val monthValue = if (days < 186) 1 + days / 31 else 7 + (days - 186) / 30
        val dayOfMonth = 1 + if (days < 186) days % 31 else (days - 186) % 30

        return JalaliDate(finalJalaliYear, monthValue, dayOfMonth)
    }

    /**
     * Returns the name of the Jalali month for a given Gregorian date.
     *
     * @param gregorianDate The Gregorian date for which to find the corresponding Jalali month
     * @return A JalaliMonth object representing the Jalali month
     */
    fun toJalaliMonth(gregorianDate: LocalDate): com.cocode.calendar.models.JalaliMonth {
        val jalaliDate = gregorianToJalali(gregorianDate)
        // 'month' is the Jalali month number. Use it to get the month name from the centralized Strings object.
        // Adjust for zero-based index
        val monthName = utils.Strings.Months.JALALI_PERSIAN[jalaliDate.monthValue - 1]

        return com.cocode.calendar.models.JalaliMonth(monthName, jalaliDate.monthValue, jalaliDate.year)
    }
}
