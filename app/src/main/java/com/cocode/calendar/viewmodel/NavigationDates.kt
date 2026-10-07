package com.cocode.calendar.viewmodel

import java.time.LocalDate
import java.time.YearMonth

/**
 * The date the main screen stores after the user moves to another Gregorian month or year.
 * Pure Kotlin: the view model and the month and year pickers use the same rules.
 */
object NavigationDates {

    /** The month the user is in today shows today's date; any other month shows its first day. */
    fun forMonth(yearMonth: YearMonth, today: LocalDate = LocalDate.now()): LocalDate =
        if (yearMonth == YearMonth.from(today)) today else yearMonth.atDay(1)

    /** The same month in another year, on its first day. */
    fun forYear(current: LocalDate, year: Int): LocalDate =
        current.withYear(year).withDayOfMonth(1)
}
