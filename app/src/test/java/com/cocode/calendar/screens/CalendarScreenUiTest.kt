package com.cocode.calendar.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.converter.openConverterViewModel
import com.cocode.calendar.converter.setConverterContent
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** How the calendar screen uses a tall phone screen, and where its About link is. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class CalendarScreenUiTest {

    @get:Rule
    val rule = createComposeRule()

    private fun calendarViewModel(): CalendarViewModel = openConverterViewModel().also { it.toggleConverter() }

    private fun show(fontScale: Float = 1f) =
        rule.setConverterContent(calendarViewModel(), fontScale) { CalendarScreen() }

    @Test
    fun `on a tall screen the day cells grow taller than their 60 dp minimum`() {
        show()
        val day = rule.onAllNodesWithText("15")[0].getBoundsInRoot()
        assertTrue("day cell height ${day.bottom - day.top}", (day.bottom - day.top).value > 61f)
    }

    @Test
    fun `the footer does not link away from the app`() {
        show()
        rule.onNodeWithText("Babak Bandpey", substring = true).assertDoesNotExist()
    }

    @Test
    fun `the footer link opens the About screen`() {
        show()
        rule.onNodeWithText("About this app").performClick()
        rule.onNodeWithText("About Jalali Persian Calendar").assertIsDisplayed()
    }

    @Test
    fun `at 200 percent text the About link can still be reached`() {
        show(2f)
        rule.onNodeWithText("About this app").performScrollTo().assertIsDisplayed()
    }
}
