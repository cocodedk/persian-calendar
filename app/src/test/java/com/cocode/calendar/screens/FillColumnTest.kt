package com.cocode.calendar.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** How [FillColumn] shares the height of the screen between its three parts. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class FillColumnTest {

    @get:Rule
    val rule = createComposeRule()

    private fun fillHeight(viewport: Dp, top: Dp, bottom: Dp, fillMin: Dp): Dp {
        rule.setContent {
            FillColumn(
                viewportHeight = viewport,
                fillMinHeight = fillMin,
                modifier = Modifier.fillMaxWidth(),
                top = { Box(Modifier.fillMaxWidth().height(top)) },
                fill = { Box(Modifier.fillMaxSize().testTag("fill")) },
                bottom = { Box(Modifier.fillMaxWidth().height(bottom)) }
            )
        }
        val bounds = rule.onNodeWithTag("fill").getBoundsInRoot()
        return bounds.bottom - bounds.top
    }

    @Test
    fun `the fill part takes the height the top and bottom parts leave`() {
        assertEquals(600f, fillHeight(800.dp, 120.dp, 80.dp, 360.dp).value, 1f)
    }

    @Test
    fun `the fill part keeps its minimum height when the top and bottom parts leave less`() {
        assertEquals(360f, fillHeight(500.dp, 200.dp, 200.dp, 360.dp).value, 1f)
    }
}
