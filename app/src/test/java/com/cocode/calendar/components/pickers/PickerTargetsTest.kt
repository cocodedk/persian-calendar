package com.cocode.calendar.components.pickers

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class PickerTargetsTest {

    // 7 October 2026 is 15 Mehr 1405
    private val today = LocalDate.of(2026, 10, 7)

    @Test
    fun `should start Farvardin at Nowruz`() {
        // 1 Farvardin 1405 is 21 March 2026
        assertEquals(LocalDate.of(2026, 3, 21), PickerTargets.jalaliMonthStart(1405, 1))
    }

    @Test
    fun `should start Dey in December and Bahman in January`() {
        // Dey runs from 22 December 2025 into January, and Bahman starts on 21 January 2026
        assertEquals(LocalDate.of(2025, 12, 22), PickerTargets.jalaliMonthStart(1404, 10))
        assertEquals(LocalDate.of(2026, 1, 21), PickerTargets.jalaliMonthStart(1404, 11))
    }

    @Test
    fun `should start Esfand in February in a leap year and in a common year`() {
        // 1403 is a leap year (30 days in Esfand), 1404 is not
        assertEquals(LocalDate.of(2025, 2, 19), PickerTargets.jalaliMonthStart(1403, 12))
        assertEquals(LocalDate.of(2026, 2, 20), PickerTargets.jalaliMonthStart(1404, 12))
    }

    @Test
    fun `should store day 1 of the chosen Jalali month`() {
        assertEquals(LocalDate.of(2026, 3, 21), PickerTargets.afterMonthChoice(today, true, 1))
        assertEquals(LocalDate.of(2026, 10, 23), PickerTargets.afterMonthChoice(today, true, 8))
    }

    @Test
    fun `should give the same date when Farvardin is picked twice in a row`() {
        val first = PickerTargets.afterMonthChoice(today, true, 1)
        val second = PickerTargets.afterMonthChoice(first, true, 1)
        val third = PickerTargets.afterMonthChoice(second, true, 1)
        assertEquals(LocalDate.of(2026, 3, 21), first)
        assertEquals(first, second)
        assertEquals(first, third)
    }

    @Test
    fun `should give the same date when Dey is picked again, and Bahman follows it`() {
        val dey = PickerTargets.afterMonthChoice(today, true, 10)
        assertEquals(LocalDate.of(2026, 12, 22), dey)
        assertEquals(dey, PickerTargets.afterMonthChoice(dey, true, 10))
        // Bahman 1405 starts on 21 January 2027
        assertEquals(LocalDate.of(2027, 1, 21), PickerTargets.afterMonthChoice(dey, true, 11))
    }

    @Test
    fun `should give the same date when Esfand of a leap year is picked again`() {
        // 7 October 2024 is 16 Mehr 1403, a leap year
        val esfand = PickerTargets.afterMonthChoice(LocalDate.of(2024, 10, 7), true, 12)
        assertEquals(LocalDate.of(2025, 2, 19), esfand)
        assertEquals(esfand, PickerTargets.afterMonthChoice(esfand, true, 12))
    }

    @Test
    fun `should keep the Jalali year when a month is picked after a year`() {
        // Year 1406 keeps Mehr: 1 Mehr 1406 is 23 September 2027
        val year = PickerTargets.afterYearChoice(today, true, 1406)
        assertEquals(LocalDate.of(2027, 9, 23), year)
        // Farvardin then stays in 1406: 1 Farvardin 1406 is 21 March 2027
        assertEquals(LocalDate.of(2027, 3, 21), PickerTargets.afterMonthChoice(year, true, 1))
    }

    @Test
    fun `should keep the Jalali month when the same year is picked again`() {
        val year = PickerTargets.afterYearChoice(today, true, 1406)
        assertEquals(year, PickerTargets.afterYearChoice(year, true, 1406))
    }

    @Test
    fun `should keep the Jalali month when a year is picked in Dey`() {
        // 25 December 2025 is 4 Dey 1404. 1 Dey 1403 is 21 December 2024.
        assertEquals(
            LocalDate.of(2024, 12, 21),
            PickerTargets.afterYearChoice(LocalDate.of(2025, 12, 25), true, 1403)
        )
    }

    @Test
    fun `should leave Gregorian month picks as they were`() {
        // another month: its first day
        assertEquals(LocalDate.of(2026, 3, 1), PickerTargets.afterMonthChoice(today, false, 3, today))
        // the month the user is in today: today
        assertEquals(today, PickerTargets.afterMonthChoice(LocalDate.of(2026, 3, 1), false, 10, today))
    }

    @Test
    fun `should leave Gregorian year picks as they were`() {
        // the same month in the chosen year, on its first day
        assertEquals(LocalDate.of(2027, 10, 1), PickerTargets.afterYearChoice(today, false, 2027))
        assertEquals(LocalDate.of(2027, 2, 1), PickerTargets.afterYearChoice(LocalDate.of(2028, 2, 29), false, 2027))
    }
}
