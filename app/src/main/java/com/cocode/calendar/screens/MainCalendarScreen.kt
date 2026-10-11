package com.cocode.calendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cocode.calendar.components.*
import com.cocode.calendar.components.about.AboutScreen
import com.cocode.calendar.converter.CalendarConverterBox
import com.cocode.calendar.components.footer.FooterInfo

/** Six weeks of 60 dp days: the grid never gets shorter than this, so large text scrolls instead. */
private val GRID_MIN_HEIGHT = 60.dp * 6

/**
 * This Composable function represents the main screen of the calendar application.
 * @Composable This annotation indicates that this function is a Composable function
 * in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarScreen() {
    // Whether the About screen is open. Saved, so it stays open when the phone is rotated.
    var showAbout by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // The screen's content keeps clear of the status and navigation bars and of a camera
        // cutout, which Android 15 and later draw over the app. The calendar grid takes the height the rest leaves, and
        // everything scrolls when large text needs more than the screen.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.displayCutout))
        ) {
            FillColumn(
                viewportHeight = maxHeight,
                fillMinHeight = GRID_MIN_HEIGHT,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                top = {
                    // The header section with gradient background, calendar info, and Iran time
                    HeaderSection()
                    // The header of the calendar view that displays the days of the week.
                    WeekDaysHeader()
                },
                fill = { CalendarGrid(Modifier.fillMaxSize()) },
                bottom = {
                    // Today, converter and calendar buttons, then month and year selection
                    CalControls()
                    CalendarNavigation()
                    FooterInfo(onAboutClick = { showAbout = true })
                }
            )
        }

        // Event creation dialog
        EventCreationDialog()

        // Event list dialog
        EventListDialog()

        // Date converter overlay, drawn over the calendar
        CalendarConverterBox()

        // About screen, drawn over everything else
        if (showAbout) {
            AboutScreen(onClose = { showAbout = false })
        }
    }
}
