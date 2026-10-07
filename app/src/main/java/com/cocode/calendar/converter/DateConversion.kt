package com.cocode.calendar.converter

import CalendarConverter
import java.time.LocalDate

/** Converts the text typed into the year, month and day fields. */
object DateConversion {

    /**
     * @return a [LocalDate] when [fromJalali], otherwise a [com.cocode.calendar.models.JalaliDate];
     *         null while a field is empty or when the date does not exist (a Jalali day is checked
     *         against the real length of its month, so Esfand 30 is valid only in a leap year).
     */
    fun convert(fromJalali: Boolean, year: String, month: String, day: String): Any? {
        val y = year.toIntOrNull() ?: return null
        val m = month.toIntOrNull() ?: return null
        val d = day.toIntOrNull() ?: return null
        if (fromJalali && !JalaliCalendar.isValidDate(y, m, d)) return null
        return try {
            if (fromJalali) CalendarConverter.jalaliToGregorian(y, m, d)
            else CalendarConverter.gregorianToJalali(LocalDate.of(y, m, d))
        } catch (e: Exception) {
            null
        }
    }
}
