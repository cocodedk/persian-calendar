package com.cocode.calendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cocode.calendar.components.*
import com.cocode.calendar.converter.CalendarConverterBox
import com.cocode.calendar.components.footer.FooterInfo

/**
 * This Composable function represents the main screen of the calendar application.
 * @Composable This annotation indicates that this function is a Composable function
 * in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarScreen() {

    // Use Box to allow absolute positioning
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content in a Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Leave space for footer
        ) {
            // The header section with gradient background, calendar info, and Iran time
            HeaderSection()

            // The header of the calendar view that displays the days of the week.
            WeekDaysHeader()

            // The grid of the calendar view that displays the dates.
            CalendarGrid()

            // The controls for the calendar view, including a button to navigate to today's date
            CalControls()

            // Month and Year selection navigation
            CalendarNavigation()
        }

        // Date converter overlay - positioned on top of everything
        CalendarConverterBox()

        // Event creation dialog
        EventCreationDialog()

        // Event list dialog
        EventListDialog()

        // Footer positioned at the bottom
        FooterInfo(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
