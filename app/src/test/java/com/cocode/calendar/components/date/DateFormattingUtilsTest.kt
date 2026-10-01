package com.cocode.calendar.components.date

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateFormattingUtilsTest {

    @Test
    fun `should call input complete when year month and day are filled`() {
        assertTrue(DateFormattingUtils.hasCompleteInput("1405", "7", "1"))
    }

    @Test
    fun `should not call input complete while the day is empty`() {
        assertFalse(DateFormattingUtils.hasCompleteInput("1405", "7", ""))
    }
}
