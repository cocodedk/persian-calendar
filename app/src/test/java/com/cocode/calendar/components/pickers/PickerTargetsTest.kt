package com.cocode.calendar.components.pickers

import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class PickerTargetsTest {

    @Test
    fun `should open March for Farvardin, which starts at Nowruz`() {
        // 1 Farvardin 1405 is 21 March 2026
        assertEquals(YearMonth.of(2026, 3), PickerTargets.gregorianMonthOfJalaliMonth(1405, 1))
    }

    @Test
    fun `should open December for Dey, which starts before the Gregorian new year`() {
        // 1 Dey 1404 is 22 December 2025, and Dey runs into January
        assertEquals(YearMonth.of(2025, 12), PickerTargets.gregorianMonthOfJalaliMonth(1404, 10))
    }

    @Test
    fun `should open January for Bahman, the month after Dey`() {
        // 1 Bahman 1404 is 21 January 2026
        assertEquals(YearMonth.of(2026, 1), PickerTargets.gregorianMonthOfJalaliMonth(1404, 11))
    }

    @Test
    fun `should open February for Esfand of a leap year`() {
        // 1403 is a leap year: Esfand has 30 days and 1 Esfand 1403 is 19 February 2025
        assertEquals(YearMonth.of(2025, 2), PickerTargets.gregorianMonthOfJalaliMonth(1403, 12))
    }

    @Test
    fun `should open February for Esfand of a common year`() {
        // 1 Esfand 1404 is 20 February 2026
        assertEquals(YearMonth.of(2026, 2), PickerTargets.gregorianMonthOfJalaliMonth(1404, 12))
    }

    @Test
    fun `should open March 2025 for Farvardin 1404`() {
        // 1 Farvardin 1404 is 21 March 2025
        assertEquals(YearMonth.of(2025, 3), PickerTargets.gregorianMonthOfJalaliMonth(1404, 1))
    }

    @Test
    fun `should keep the Jalali year of the screen when a Jalali month is chosen`() {
        // 7 October 2026 is 15 Mehr 1405, so Aban 1405 starts on 23 October 2026
        assertEquals(
            YearMonth.of(2026, 10),
            PickerTargets.afterJalaliMonthChoice(LocalDate.of(2026, 10, 7), 8)
        )
        // and Farvardin 1405 starts in March 2026, in the same Jalali year
        assertEquals(
            YearMonth.of(2026, 3),
            PickerTargets.afterJalaliMonthChoice(LocalDate.of(2026, 10, 7), 1)
        )
    }

    @Test
    fun `should use the Jalali year in force at the date on screen when a month is chosen in January`() {
        // 15 January 2026 is in Dey 1404, so choosing Farvardin goes to March 2025
        assertEquals(
            YearMonth.of(2025, 3),
            PickerTargets.afterJalaliMonthChoice(LocalDate.of(2026, 1, 15), 1)
        )
    }

    @Test
    fun `should keep the Jalali month of the screen when a Jalali year is chosen`() {
        // 7 October 2026 is in Mehr (month 7). 1 Mehr 1406 is 23 September 2027.
        assertEquals(
            YearMonth.of(2027, 9),
            PickerTargets.afterJalaliYearChoice(LocalDate.of(2026, 10, 7), 1406)
        )
    }

    @Test
    fun `should keep Dey when a year is chosen while Dey is on screen`() {
        // 25 December 2025 is in Dey 1404. 1 Dey 1403 is 21 December 2024.
        assertEquals(
            YearMonth.of(2024, 12),
            PickerTargets.afterJalaliYearChoice(LocalDate.of(2025, 12, 25), 1403)
        )
    }
}
