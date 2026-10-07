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
        setKeyboard(height = 0) // Robolectric starts with the keyboard counted as up
        rule.enterDate("1402", "12", "30")
        rule.title().performScrollTo()
        rule.onNodeWithText("Invalid Date").assertIsNotDisplayed()

        setKeyboard(height = 100)
        rule.onNodeWithText("Invalid Date").assertIsDisplayed()
    }

    @Test
    fun `the message is brought into view again as the keyboard grows while it stays up`() {
        show()
        setKeyboard(height = 0)
        rule.enterDate("1402", "12", "30")
        rule.title().performScrollTo()

        setKeyboard(height = 100) // the keyboard starts to slide in
        rule.onNodeWithText("Invalid Date").assertIsDisplayed()

        rule.title().performScrollTo() // the further shrink moves the message out of sight again
        rule.onNodeWithText("Invalid Date").assertIsNotDisplayed()
        setKeyboard(height = 600) // still up, now at its full height
        rule.onNodeWithText("Invalid Date").assertIsDisplayed()
    }

    /** Tells the window the keyboard is [height] px tall; 0 means it is down. */
    private fun setKeyboard(height: Int) {
        val insets = WindowInsetsCompat.Builder()
            .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, height))
            .setVisible(WindowInsetsCompat.Type.ime(), height > 0)
            .build()
        rule.runOnUiThread {
            val content = rule.activity.findViewById<ViewGroup>(android.R.id.content)
            ViewCompat.dispatchApplyWindowInsets(content.getChildAt(0), insets)
        }
        rule.waitForIdle()
    }
}
