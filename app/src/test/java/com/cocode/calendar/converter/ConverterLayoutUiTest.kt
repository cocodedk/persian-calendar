package com.cocode.calendar.converter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** How the converter card is laid out at the normal and at the largest text size. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class ConverterLayoutUiTest {

    @get:Rule
    val rule = createComposeRule()

    private fun show(fontScale: Float) = rule.setConverterContent(openConverterViewModel(), fontScale) {
        CalendarConverterBox()
    }

    @Test
    fun `at normal text the Year Month and Day fields sit in one row`() {
        show(1f)
        val year = rule.field("Year").bounds()
        val month = rule.field("Month").bounds()
        val day = rule.field("Day").bounds()
        assertEquals(year.top, month.top, 1f)
        assertEquals(year.top, day.top, 1f)
        assertTrue(month.left >= year.right && day.left >= month.right)
    }

    @Test
    fun `at 200 percent text the fields are stacked in the same order`() {
        show(2f)
        val year = rule.field("Year").bounds()
        val month = rule.field("Month").bounds()
        val day = rule.field("Day").bounds()
        assertTrue("Month under Year", month.top >= year.bottom)
        assertTrue("Day under Month", day.top >= month.bottom)
        assertEquals(year.left, month.left, 1f)
        assertEquals(year.right, month.right, 1f)
    }

    @Test
    fun `at normal text the title and both buttons share one line`() {
        show(1f)
        val title = rule.title().bounds()
        assertEquals(title.center.y, rule.swapButton().bounds().center.y, title.height)
        assertTrue(rule.swapButton().bounds().left >= title.right)
        assertTrue(rule.closeButton().bounds().left >= rule.swapButton().bounds().right)
    }

    @Test
    fun `at 200 percent text both buttons wrap under the title and stay inside the card`() {
        show(2f)
        val title = rule.title().bounds()
        val swap = rule.swapButton().bounds()
        val close = rule.closeButton().bounds()
        val card = rule.onNodeWithText("Jalali → Gregorian").bounds() // as wide as the card's content
        assertTrue("buttons under the title", swap.top >= title.bottom && close.top >= title.bottom)
        for (button in listOf(swap, close)) {
            assertTrue("inside the card: $button vs $card", button.left >= card.left && button.right <= card.right)
        }
    }

    @Test
    fun `at 200 percent text the swap and close buttons work`() {
        show(2f)
        rule.swapButton().performClick()
        rule.onNodeWithText("Gregorian → Jalali").assertIsDisplayed()
        rule.swapButton().performClick()
        rule.onNodeWithText("Jalali → Gregorian").assertIsDisplayed()
        rule.closeButton().performClick()
        rule.title().assertDoesNotExist()
    }
}
