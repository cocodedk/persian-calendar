package com.cocode.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import utils.Strings
import java.time.LocalDate

/**
 * Displays the control buttons for the calendar application.
 *
 * This composable function creates a row containing three buttons:
 * - A "Today" button to reset the calendar to the current date
 * - A toggle button for the date converter
 * - A toggle button to switch between Gregorian and Jalali calendars
 *
 * The buttons are arranged with space between them and vertically centered.
 *
 * @Composable This function is a Jetpack Compose composable.
 */
@Composable
fun CalControls() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(left = 2.dp, top = 1.dp, right = 2.dp, bottom = 0.dp)
    ) {
        TodayButton()
        DateConverterToggleButton()
        CalendarToggleButton()
    }
}

/**
 * This Composable function represents a button that updates the current date to today's date.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose,
 * a modern toolkit for building native Android UI.
 */
@Composable
fun TodayButton() {
    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()

    // Center the button in the row
    Box {
        // Create a Button Composable
        Button(
            // Set the click event handler for the button
            // When the button is clicked, it calls the updateGregorianDate function of the CalendarViewModel with today's date
            onClick = { viewModel.updateGregorianDate(LocalDate.now()) },
            colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background),
            shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 10.dp),
            modifier = Modifier
                .width(130.dp)
                .height(52.dp)
        ) {
            // Set the display text for the button
            Text(
                text = Strings.Calendar.TODAY,
                color = CalColors.text,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

/**
 * A composable function that creates a toggle button for the date converter.
 *
 * This button allows users to show or hide the date converter interface. The button's appearance
 * changes based on whether the converter is currently visible or not.
 *
 * The function doesn't explicitly return a value, but it creates and displays a Button composable
 * as part of the Jetpack Compose UI.
 */
@Composable
fun DateConverterToggleButton() {
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()
    Box {
        Button(
            onClick = {
                viewModel.toggleConverter()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showConverter)
                    CalColors.active_button_background
                else CalColors.button_background
            ),
            shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
            modifier = Modifier
                .width(130.dp)
                .height(52.dp)
        ) {
            Text(
                text = "Converter",
                color = CalColors.text,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * This Composable function represents a button that toggles
 * the calendar view between Gregorian and Persian (Jalali) modes.
 *
 * @Composable This annotation indicates that this function is a
 * Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarToggleButton() {

    val viewModel: CalendarViewModel = viewModel()
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    // This composable function is a button to toggle between Gregorian and Persian (Jalali) calendars.
    Box {
        Button(
            onClick = {viewModel.toggleIsJalaliCalendar()},
            colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background),
            shape = RoundedCornerShape(0.dp, 0.dp, 10.dp, 0.dp),
            modifier = Modifier
                .width(192.dp)
                .height(52.dp)

        ) {
            Text(
                text = if (isJalaliCalendar) Strings.Calendar.GREGORIAN else Strings.Calendar.JALALI,
                color = CalColors.text,
                fontWeight = FontWeight.Bold

            )
        }
    }
}
