package com.cocode.calendar.components

import androidx.compose.runtime.Composable

/**
 * GridComponents.kt - Facade for calendar grid components.
 *
 * This file now acts as a simple facade that delegates to the specialized
 * grid components for better maintainability and organization.
 *
 * The implementation has been split into:
 * - components/header/CalendarHeader.kt for header components
 * - components/grid/CalendarGrid.kt for grid layout components
 * - components/cells/DayComponents.kt for individual day cells
 * - components/interfaces/CalendarComponentContracts.kt for contracts
 */

// Re-export header components for backward compatibility
@Composable
fun WeekDaysHeader() {
    com.cocode.calendar.components.header.WeekDaysHeader()
}

@Composable
fun DayOfWeekBox(day: String) {
    com.cocode.calendar.components.header.DayOfWeekBox(day)
}

// Re-export grid components for backward compatibility
@Composable
fun CalendarGrid() {
    com.cocode.calendar.components.grid.CalendarGrid()
}

@Composable
fun WeekRow(
    startDate: java.time.LocalDate,
    daysInWeek: Int,
    yearMonth: java.time.YearMonth,
    allEvents: List<com.cocode.calendar.Event>
) {
    com.cocode.calendar.components.grid.WeekRow(
        startDate = startDate,
        daysInWeek = daysInWeek,
        yearMonth = yearMonth,
        allEvents = allEvents
    )
}

// Re-export cell components for backward compatibility
@Composable
@Suppress("UNUSED_PARAMETER")
fun DayBox(
    currentDate: java.time.LocalDate,
    isInCurrentMonth: Boolean,
    jalaliDate: com.cocode.calendar.models.JalaliDate,
    events: List<com.cocode.calendar.Event>
) {
    com.cocode.calendar.components.cells.DayBox(
        currentDate = currentDate,
        isInCurrentMonth = isInCurrentMonth,
        events = events
    )
}
