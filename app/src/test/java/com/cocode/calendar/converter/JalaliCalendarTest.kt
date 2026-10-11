package com.cocode.calendar.converter

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JalaliCalendarTest {

    @Test
    fun `should call 1399, 1403 and 1408 leap years`() {
        listOf(1399, 1403, 1408).forEach { assertTrue("$it is leap", JalaliCalendar.isLeapYear(it)) }
    }

    @Test
    fun `should call 1402, 1404 and 1405 common years`() {
        listOf(1402, 1404, 1405).forEach { assertFalse("$it is common", JalaliCalendar.isLeapYear(it)) }
    }

    @Test
    fun `should give months 1 to 6 thirty-one days and months 7 to 11 thirty`() {
        (1..6).forEach { assertEquals(31, JalaliCalendar.daysInMonth(1402, it)) }
        (7..11).forEach { assertEquals(30, JalaliCalendar.daysInMonth(1402, it)) }
    }

    @Test
    fun `should give Esfand 30 days in a leap year and 29 in a common year`() {
        assertEquals(30, JalaliCalendar.daysInMonth(1403, 12))
        assertEquals(29, JalaliCalendar.daysInMonth(1402, 12))
    }

    @Test
    fun `should have no day 30 in Esfand 1402`() {
        assertTrue(JalaliCalendar.isValidDate(1402, 12, 29))
        assertFalse(JalaliCalendar.isValidDate(1402, 12, 30))
        assertTrue(JalaliCalendar.isValidDate(1403, 12, 30))
    }

    @Test
    fun `should reject a day or month out of range`() {
        assertFalse(JalaliCalendar.isValidDate(1405, 7, 31))
        assertFalse(JalaliCalendar.isValidDate(1405, 1, 0))
        assertFalse(JalaliCalendar.isValidDate(1405, 0, 1))
        assertFalse(JalaliCalendar.isValidDate(1405, 13, 1))
    }

    @Test
    fun `should agree with the Gregorian to Jalali converter from 1300 to 1500`() {
        for (year in 1300..1500) {
            for (month in 1..12) {
                for (day in 1..JalaliCalendar.daysInMonth(year, month)) {
                    val gregorian = JalaliToGregorianConverter.jalaliToGregorian(year, month, day)
                    val back = GregorianToJalaliConverter.gregorianToJalali(gregorian)
                    assertEquals("$year/$month/$day", Triple(year, month, day),
                        Triple(back.year, back.monthValue, back.dayOfMonth))
                }
            }
        }
    }

    @Test
    fun `should find the day after the last day of a month in the next month`() {
        // The day after the last valid day is day 1 of the next month, so no valid day is missing
        for (year in 1300..1500) {
            for (month in 1..12) {
                val last = JalaliToGregorianConverter.jalaliToGregorian(year, month, JalaliCalendar.daysInMonth(year, month))
                val nextYear = if (month == 12) year + 1 else year
                val nextMonth = if (month == 12) 1 else month + 1
                assertEquals(last.plusDays(1), JalaliToGregorianConverter.jalaliToGregorian(nextYear, nextMonth, 1))
            }
        }
    }

    @Test
    fun `should match the known dates around Nowruz 1403`() {
        assertEquals(LocalDate.of(2024, 3, 19), JalaliToGregorianConverter.jalaliToGregorian(1402, 12, 29))
        assertEquals(LocalDate.of(2024, 3, 20), JalaliToGregorianConverter.jalaliToGregorian(1403, 1, 1))
    }
}
