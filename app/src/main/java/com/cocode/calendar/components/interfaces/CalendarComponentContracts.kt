package com.cocode.calendar.components.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cocode.calendar.Event
import com.cocode.calendar.models.JalaliDate
import java.time.LocalDate
import java.time.YearMonth

/**
 * Interface for calendar header components that display week day information.
 */
interface CalendarHeaderContract {
    /**
     * Displays the week days header row.
     */
    @Composable
    fun WeekDaysHeader()

    /**
     * Displays an individual day of the week box.
     * @param day The day name to display (e.g., "Mon", "Tue")
     */
    @Composable
    fun DayOfWeekBox(day: String)
}

/**
 * Interface for calendar grid components that handle layout and navigation.
 */
interface CalendarGridContract {
    /**
     * Displays the main calendar grid with swipe gestures.
     */
    @Composable
    fun CalendarGrid()

    /**
     * Displays a row of days for a specific week.
     * @param startDate The first day of the week
     * @param daysInWeek Number of days in the week (usually 7)
     * @param yearMonth The current month being displayed
     * @param allEvents Events to display in the week
     */
    @Composable
    fun WeekRow(
        startDate: LocalDate,
        daysInWeek: Int,
        yearMonth: YearMonth,
        allEvents: List<Event>
    )
}

/**
 * Interface for calendar day cell components.
 */
interface DayCellContract {
    /**
     * Displays an individual day cell in the calendar grid.
     * @param currentDate The date to display
     * @param isInCurrentMonth Whether this date is in the currently displayed month
     * @param events Events occurring on this date
     */
    @Composable
    fun DayBox(
        currentDate: LocalDate,
        isInCurrentMonth: Boolean,
        events: List<Event>
    )
}
