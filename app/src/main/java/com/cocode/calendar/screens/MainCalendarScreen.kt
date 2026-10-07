package com.cocode.calendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.cocode.calendar.components.*
import com.cocode.calendar.components.about.AboutEntryButton
import com.cocode.calendar.components.about.AboutScreen
import com.cocode.calendar.converter.CalendarConverterBox
import com.cocode.calendar.components.footer.FooterInfo

/**
 * This Composable function represents the main screen of the calendar application.
 * @Composable This annotation indicates that this function is a Composable function
 * in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarScreen() {
    // Whether the About screen is open. Saved, so it stays open when the phone is rotated.
    var showAbout by rememberSaveable { mutableStateOf(false) }

    // The About button and the footer sit over the bottom of the screen. The content above
    // scrolls, and ends with room for them, so every control can be reached at any text size.
    val density = LocalDensity.current
    var bottomBarHeight by remember { mutableStateOf(0.dp) }

    // Use Box to allow absolute positioning
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content in a Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = bottomBarHeight)
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

        // About button and footer positioned at the bottom
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onSizeChanged { bottomBarHeight = with(density) { it.height.toDp() } }
        ) {
            AboutEntryButton(onClick = { showAbout = true })
            FooterInfo()
        }

        // About screen, drawn over everything else
        if (showAbout) {
            AboutScreen(onClose = { showAbout = false })
        }
    }
}
