package com.cocode.calendar.converter

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MonthEntryTest {

    @Test
    fun `should wait after 1 because 10 to 12 may follow`() {
        assertFalse(MonthEntry.isComplete("1"))
    }

    @Test
    fun `should finish after 2 because no month continues from it`() {
        assertTrue(MonthEntry.isComplete("2"))
    }

    @Test
    fun `should finish after two digits`() {
        assertTrue(MonthEntry.isComplete("12"))
    }

    @Test
    fun `should not finish while empty`() {
        assertFalse(MonthEntry.isComplete(""))
    }
}
