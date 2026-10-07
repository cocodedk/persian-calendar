package com.cocode.calendar.components.pickers

import com.cocode.calendar.converter.GregorianToJalaliConverter
import com.cocode.calendar.converter.JalaliToGregorianConverter
import java.time.LocalDate
import java.time.YearMonth

/**
 * Where the calendar grid goes when the user picks a Jalali month or year. The grid always
 * shows one Gregorian month, so a Jalali choice becomes "the Gregorian month that contains
 * day 1 of that Jalali month". Pure Kotlin, no Android classes.
 */
object PickerTargets {

    /** The Gregorian month that contains day 1 of Jalali month [jalaliMonth] (1-12) of [jalaliYear]. */
    fun gregorianMonthOfJalaliMonth(jalaliYear: Int, jalaliMonth: Int): YearMonth =
        YearMonth.from(JalaliToGregorianConverter.jalaliToGregorian(jalaliYear, jalaliMonth, 1))

    /**
     * Target after choosing Jalali month [jalaliMonth] while the grid shows [current]:
     * that month of the Jalali year [current] falls in.
     */
    fun afterJalaliMonthChoice(current: LocalDate, jalaliMonth: Int): YearMonth =
        gregorianMonthOfJalaliMonth(GregorianToJalaliConverter.gregorianToJalali(current).year, jalaliMonth)

    /**
     * Target after choosing Jalali year [jalaliYear] while the grid shows [current]:
     * the Jalali month [current] falls in, in the chosen year.
     */
    fun afterJalaliYearChoice(current: LocalDate, jalaliYear: Int): YearMonth =
        gregorianMonthOfJalaliMonth(jalaliYear, GregorianToJalaliConverter.gregorianToJalali(current).monthValue)
}
