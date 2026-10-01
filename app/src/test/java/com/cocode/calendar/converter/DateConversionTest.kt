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
}
