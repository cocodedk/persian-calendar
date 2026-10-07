package com.cocode.calendar.converter

import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** What the converter's fields do while the user types: focus, the Next key, and the result. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class ConverterFieldsUiTest {

    @get:Rule
    val rule = createComposeRule()

    private fun show(jalaliToGregorian: Boolean = true) {
        val viewModel = openConverterViewModel()
        if (!jalaliToGregorian) viewModel.toggleJalaliToGregorianConverter()
        rule.setConverterContent(viewModel) { DateConverter(jalaliToGregorian, !jalaliToGregorian) }
    }

    @Test
    fun `typing 1 then 2 in Month gives 12 and moves to Day only after the 2`() {
        show()
        rule.field("Year").performTextInput("1403")
        rule.field("Month").assertIsFocused()

        rule.field("Month").performTextInput("1")
        rule.field("Month").assertIsFocused()
        rule.field("Day").assertIsNotFocused()

        rule.field("Month").performTextInput("2")
        rule.field("Month").assertTextContains("12")
        rule.field("Day").assertIsFocused()
    }

    @Test
    fun `a Month digit from 2 to 9 moves on to Day at once`() {
        show()
        rule.field("Year").performTextInput("1403")
        rule.field("Month").performTextInput("5")
        rule.field("Day").assertIsFocused()
    }

    @Test
    fun `Next on Year and on Month moves exactly one field`() {
        show()
        rule.field("Year").performTextInput("14")
        rule.field("Year").performImeAction()
        rule.field("Month").assertIsFocused()
        rule.field("Day").assertIsNotFocused()

        rule.field("Month").performTextInput("1")
        rule.field("Month").performImeAction()
        rule.field("Day").assertIsFocused()
    }

    @Test
    fun `Jalali 1402 12 30 is invalid and 1403 12 30 gives 2025-03-20`() {
        show()
        rule.enterDate("1402", "12", "30")
        rule.onNodeWithText("Invalid Date").assertExists()
        rule.onNodeWithText("Enter a valid Jalali date").assertExists()

        rule.enterDate("1403", "12", "30")
        rule.onNodeWithText("2025-03-20").assertExists()
        rule.onNodeWithText("Invalid Date").assertDoesNotExist()
    }

    @Test
    fun `Gregorian to Jalali converts a real date and rejects one that does not exist`() {
        show(jalaliToGregorian = false)
        rule.enterDate("2024", "2", "29")
        rule.onNodeWithText("1402/12/10").assertExists()

        rule.enterDate("2024", "2", "30")
        rule.onNodeWithText("Invalid Date").assertExists()
        rule.onNodeWithText("Enter a valid Gregorian date").assertExists()

        rule.enterDate("2023", "2", "29")
        rule.onNodeWithText("Invalid Date").assertExists()
    }

    @Test
    fun `the invalid message waits until year month and day are all filled`() {
        show()
        rule.field("Year").performTextInput("1402")
        rule.field("Month").performTextInput("12")
        rule.onNodeWithText("Invalid Date").assertDoesNotExist()
    }
}
