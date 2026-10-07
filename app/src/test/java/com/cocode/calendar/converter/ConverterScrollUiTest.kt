package com.cocode.calendar.converter

import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** A screen so short that the card must scroll, as when the keyboard is open at a large text size. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h480dp-xxhdpi")
class ConverterScrollUiTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun show() =
        rule.setConverterContent(openConverterViewModel(), fontScale = 2f) { CalendarConverterBox() }

    @Test
    fun `a result is brought into view when the date is completed`() {
        show()
        rule.enterDate("1403", "12", "30")
        rule.onNodeWithText("2025-03-20").assertIsDisplayed()
    }

    @Test
    fun `the invalid message is brought into view when an invalid date is edited to another one`() {
        show()
        rule.enterDate("1402", "12", "30")
        rule.onNodeWithText("Invalid Date").assertIsDisplayed()

        rule.title().performScrollTo() // back to the top: the message is out of sight
        rule.onNodeWithText("Invalid Date").assertIsNotDisplayed()

        rule.field("Year").performTextReplacement("1404") // 1404 is not a leap year either
        rule.onNodeWithText("Enter a valid Jalali date").assertIsDisplayed()
    }

    @Test
    fun `the message is brought into view when the keyboard opens`() {
        show()
        setKeyboard(visible = false) // Robolectric starts with the keyboard counted as up
        rule.enterDate("1402", "12", "30")
        rule.title().performScrollTo()
        rule.onNodeWithText("Invalid Date").assertIsNotDisplayed()

        setKeyboard(visible = true)
        rule.onNodeWithText("Invalid Date").assertIsDisplayed()
    }

    /** Tells the window that the keyboard is up or down, the way the system does. */
    private fun setKeyboard(visible: Boolean) {
        val insets = WindowInsetsCompat.Builder()
            .setInsets(WindowInsetsCompat.Type.ime(), if (visible) Insets.of(0, 0, 0, 100) else Insets.NONE)
            .setVisible(WindowInsetsCompat.Type.ime(), visible)
            .build()
        rule.runOnUiThread {
            val content = rule.activity.findViewById<ViewGroup>(android.R.id.content)
            ViewCompat.dispatchApplyWindowInsets(content.getChildAt(0), insets)
        }
        rule.waitForIdle()
    }
}
