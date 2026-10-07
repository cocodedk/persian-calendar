package com.cocode.calendar.components.pickers

import com.cocode.calendar.converter.GregorianToJalaliConverter
import com.cocode.calendar.converter.JalaliToGregorianConverter
import com.cocode.calendar.viewmodel.NavigationDates
import java.time.LocalDate
import java.time.YearMonth

/**
 * The date the main screen stores after a month or year is picked. The grid always shows the
 * Gregorian month of that date, and the Jalali month and year the pickers highlight are read
 * from it too. A Jalali pick stores the Gregorian date of day 1 of the chosen Jalali month, so
 * repeating a pick gives the same date. Pure Kotlin, no Android classes.
 */
object PickerTargets {

    /** The Gregorian date of day 1 of Jalali month [jalaliMonth] (1-12) in [jalaliYear]. */
    fun jalaliMonthStart(jalaliYear: Int, jalaliMonth: Int): LocalDate =
        JalaliToGregorianConverter.jalaliToGregorian(jalaliYear, jalaliMonth, 1)

    /**
     * The date to store after month [month] (1-12) is picked in the calendar the pickers show,
     * while the screen is on [current]. In Jalali mode it is that month of the Jalali year
     * [current] falls in. Gregorian mode keeps the long-standing rule (see [NavigationDates]).
     */
    fun afterMonthChoice(
        current: LocalDate,
        isJalaliCalendar: Boolean,
        month: Int,
        today: LocalDate = LocalDate.now()
    ): LocalDate =
        if (isJalaliCalendar) {
            jalaliMonthStart(GregorianToJalaliConverter.gregorianToJalali(current).year, month)
        } else {
            NavigationDates.forMonth(YearMonth.of(current.year, month), today)
        }

    /**
     * The date to store after [year] is picked in the calendar the pickers show, while the
     * screen is on [current]. In Jalali mode it is the Jalali month [current] falls in, in
     * that year. Gregorian mode keeps the long-standing rule (see [NavigationDates]).
     */
    fun afterYearChoice(current: LocalDate, isJalaliCalendar: Boolean, year: Int): LocalDate =
        if (isJalaliCalendar) {
            jalaliMonthStart(year, GregorianToJalaliConverter.gregorianToJalali(current).monthValue)
        } else {
            NavigationDates.forYear(current, year)
        }
}
