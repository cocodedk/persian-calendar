package com.cocode.calendar.converter

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class JalaliToGregorianConverterTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should refuse Esfand 30 in a common year`() {
        JalaliToGregorianConverter.jalaliToGregorian(1402, 12, 30)
    }

    @Test
    fun `should accept Esfand 30 in a leap year`() {
        assertEquals(LocalDate.of(2025, 3, 20), JalaliToGregorianConverter.jalaliToGregorian(1403, 12, 30))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should refuse day 31 in a 30-day month`() {
        JalaliToGregorianConverter.jalaliToGregorian(1403, 7, 31)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should refuse day 0`() {
        JalaliToGregorianConverter.jalaliToGregorian(1403, 1, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should refuse month 13`() {
        JalaliToGregorianConverter.jalaliToGregorian(1403, 13, 1)
    }

    @Test
    fun `should accept every real date from 1950 to 2100`() {
        val rejected = generateSequence(LocalDate.of(1950, 1, 1)) { it.plusDays(1) }
            .takeWhile { it.year <= 2100 }
            .filter { gregorian ->
                val jalali = GregorianToJalaliConverter.gregorianToJalali(gregorian)
                runCatching {
                    JalaliToGregorianConverter.jalaliToGregorian(jalali.year, jalali.monthValue, jalali.dayOfMonth)
                }.getOrNull() != gregorian
            }
            .toList()
        assertEquals(emptyList<LocalDate>(), rejected)
    }
}
