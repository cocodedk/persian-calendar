package com.cocode.calendar.screens

import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.converter.openConverterViewModel
import com.cocode.calendar.converter.setConverterContent
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** How the calendar screen fits narrow phones and keeps clear of the system bars. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w360dp-h780dp-xxhdpi")
class CalendarScreenFitUiTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    /** October 2026, in which the 14th is a Wednesday. */
    private fun show() {
        val viewModel: CalendarViewModel = openConverterViewModel().also {
            it.toggleConverter()
            it.changeMonth(YearMonth.of(2026, 10))
        }
        rule.setConverterContent(viewModel) { CalendarScreen() }
    }

    @Test
    fun `on a 360 dp phone each week day label sits over its column`() {
        show()
        val label = rule.onNodeWithText("Wed").getBoundsInRoot()
        val day = rule.onNodeWithText("14").getBoundsInRoot()
        assertEquals(((day.left + day.right) / 2).value, ((label.left + label.right) / 2).value, 2f)
    }

    @Test
    fun `the calendar keeps clear of the status bar and the navigation bar`() {
        show()
        setSystemBars(top = 150, bottom = 200)
        val header = rule.onNodeWithText("October 2026", substring = true).getBoundsInRoot()
        val about = rule.onNodeWithText("About this app").getBoundsInRoot()
        val screen = rule.onRoot().getBoundsInRoot()
        val density = rule.activity.resources.displayMetrics.density
        assertTrue(
            "header top ${header.top}, about bottom ${about.bottom}, screen bottom ${screen.bottom}",
            header.top.value * density >= 150 && (screen.bottom - about.bottom).value * density >= 200
        )
    }

    /** Tells the window the status bar is [top] px and the navigation bar [bottom] px tall. */
    private fun setSystemBars(top: Int, bottom: Int) {
        val insets = WindowInsetsCompat.Builder()
            .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.of(0, top, 0, 0))
            .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, bottom))
            .build()
        rule.runOnUiThread {
            val content = rule.activity.findViewById<ViewGroup>(android.R.id.content)
            ViewCompat.dispatchApplyWindowInsets(content.getChildAt(0), insets)
        }
        rule.waitForIdle()
    }
}
