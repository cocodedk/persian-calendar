package com.cocode.calendar.converter

import com.cocode.calendar.models.JalaliDate
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DateConversionTest {

    @Test
    fun `should convert complete Jalali input to Gregorian`() {
        assertEquals(LocalDate.of(2026, 9, 23), DateConversion.convert(true, "1405", "7", "1"))
    }

    @Test
    fun `should convert complete Gregorian input to Jalali`() {
        assertEquals(JalaliDate(1405, 7, 1), DateConversion.convert(false, "2026", "9", "23"))
    }

    @Test
    fun `should return nothing while a field is still empty`() {
        assertNull(DateConversion.convert(true, "1405", "", ""))
    }

    @Test
    fun `should return nothing for a Gregorian date that does not exist`() {
        assertNull(DateConversion.convert(false, "2026", "2", "30"))
    }

    @Test
    fun `should reject day 30 of Esfand in a common Jalali year`() {
        // Esfand 1402 has 29 days: 1402/12/29 is 19 March 2024 and day 1 of 1403 is 20 March
        assertNull(DateConversion.convert(true, "1402", "12", "30"))
        assertNull(DateConversion.convert(true, "1404", "12", "30"))
        assertEquals(LocalDate.of(2024, 3, 19), DateConversion.convert(true, "1402", "12", "29"))
    }

    @Test
    fun `should accept day 30 of Esfand in a leap Jalali year`() {
        assertEquals(LocalDate.of(2025, 3, 20), DateConversion.convert(true, "1403", "12", "30"))
        assertEquals(LocalDate.of(2020, 3, 19), DateConversion.convert(true, "1398", "12", "29"))
        assertEquals(LocalDate.of(2021, 3, 20), DateConversion.convert(true, "1399", "12", "30"))
        assertEquals(LocalDate.of(2030, 3, 20), DateConversion.convert(true, "1408", "12", "30"))
    }

    @Test
    fun `should reject day 31 in the months that have 30 days`() {
        assertNull(DateConversion.convert(true, "1405", "7", "31"))
        assertNull(DateConversion.convert(true, "1405", "11", "31"))
        assertEquals(LocalDate.of(2026, 9, 22), DateConversion.convert(true, "1405", "6", "31"))
    }

    @Test
    fun `should still reject a Gregorian February 30 and a 29 in a common year`() {
        assertNull(DateConversion.convert(false, "2024", "2", "30"))
        assertNull(DateConversion.convert(false, "2023", "2", "29"))
        assertEquals(JalaliDate(1402, 12, 10), DateConversion.convert(false, "2024", "2", "29"))
    }
}
