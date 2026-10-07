package com.cocode.calendar.converter

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldAdvanceTest {

    private fun month(text: String) = FieldAdvance.isFull(text, FieldAdvance.MONTH_MAX)
    private fun day(text: String) = FieldAdvance.isFull(text, FieldAdvance.DAY_MAX)

    @Test
    fun `should wait after a month that starts with 1`() {
        // "1" may become 10, 11 or 12, so typing "1" and then "2" must give month 12
        assertFalse(month("1"))
        assertTrue(month("12"))
    }

    @Test
    fun `should move on after a month digit from 2 to 9`() {
        ('2'..'9').forEach { assertTrue("month $it", month(it.toString())) }
    }

    @Test
    fun `should move on after two month digits`() {
        listOf("10", "11", "12").forEach { assertTrue("month $it", month(it)) }
    }

    @Test
    fun `should wait for a day that starts with 1 to 3`() {
        ('1'..'3').forEach { assertFalse("day $it", day(it.toString())) }
    }

    @Test
    fun `should move on after a day digit from 4 to 9`() {
        ('4'..'9').forEach { assertTrue("day $it", day(it.toString())) }
    }

    @Test
    fun `should move on after two day digits`() {
        listOf("10", "29", "30", "31").forEach { assertTrue("day $it", day(it)) }
    }

    @Test
    fun `should not move on for an empty or zero field`() {
        assertFalse(month(""))
        assertFalse(month("0"))
        assertFalse(day(""))
        assertFalse(day("0"))
    }
}
