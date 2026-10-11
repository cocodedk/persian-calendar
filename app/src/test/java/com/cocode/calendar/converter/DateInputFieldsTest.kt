package com.cocode.calendar.converter

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateInputFieldsTest {

    // A 700 px row with 12 px gaps and 17 px of label padding on each side. One unit of width is
    // (700 - 24) divided by 3.5, 193 px. Month and Day get one unit, so a label up to 159 px fits;
    // Year gets 1.5 units, so a label up to 255 px fits.
    private fun fit(year: Float, month: Float, day: Float) =
        labelsFit(listOf(year, month, day), rowWidth = 700f, gap = 12f, labelPadding = 17f)

    @Test
    fun `should keep the fields side by side while every label fits`() {
        assertTrue(fit(year = 60f, month = 110f, day = 70f))
        assertTrue(fit(year = 255f, month = 159f, day = 159f))
    }

    @Test
    fun `should stack the fields when the Month label does not fit`() {
        // the label that broke as "Mo / nth" at 200% text
        assertFalse(fit(year = 100f, month = 160f, day = 80f))
    }

    @Test
    fun `should stack the fields when the Year or the Day label does not fit`() {
        assertFalse(fit(year = 256f, month = 100f, day = 80f))
        assertFalse(fit(year = 100f, month = 100f, day = 160f))
    }
}
