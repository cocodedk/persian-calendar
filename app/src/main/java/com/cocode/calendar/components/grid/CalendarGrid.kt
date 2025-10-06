package com.cocode.calendar.components.grid

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.Event
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

/**
 * This Composable function represents the grid of the calendar view that displays the dates.
 * It observes the current Gregorian date from the ViewModel and generates a grid of dates for the current month.
 * The grid includes dates from the previous month and the next month to fill the entire grid.
 * Each date in the grid is a Composable function that represents a day in the calendar.
 * The grid is always exactly 6 rows (42 days) for consistent layout.
 *
 * Features swipe gestures for intuitive month navigation:
 * - Swipe left to go to next month
 * - Swipe right to go to previous month
 */
@Composable
fun CalendarGrid() {
    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    // Observe the gregorianDate LiveData from the ViewModel
    val gregorianDate = viewModel.gregorianDate.observeAsState(initial = LocalDate.now()).value
    // Observe events from the ViewModel
    val events = viewModel.events.collectAsState().value

    // Get the YearMonth from the observed gregorianDate
    val yearMonth = YearMonth.from(gregorianDate)
    // Get the maximum number of days in a week
    val daysInWeek = WeekFields.of(Locale.getDefault()).dayOfWeek().range().maximum.toInt()
    // Get the first day of the current month
    val firstDayOfMonth = yearMonth.atDay(1)

    // Calculate the start day to ensure exactly 6 weeks (42 days) are always displayed
    val startDayOfWeek = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

    // Swipe threshold for month navigation (minimum pixels to trigger navigation)
    val swipeThreshold = 100f

    // Track total drag distance for proper swipe detection
    var totalDragDistance by remember { mutableStateOf(0f) }

    // Create a Column Composable for the calendar grid with swipe gesture support
    Column(
        modifier = Modifier.pointerInput(yearMonth) {
            detectHorizontalDragGestures(
                onDragStart = {
                    totalDragDistance = 0f
                },
                onDragEnd = {
                    // Only trigger navigation if the total drag distance exceeds threshold
                    if (totalDragDistance > swipeThreshold) {
                        // Swipe right - go to previous month
                        viewModel.changeMonth(yearMonth.minusMonths(1))
                    } else if (totalDragDistance < -swipeThreshold) {
                        // Swipe left - go to next month
                        viewModel.changeMonth(yearMonth.plusMonths(1))
                    }
                    totalDragDistance = 0f
                }
            ) { _, dragAmount ->
                // Accumulate drag distance
                totalDragDistance += dragAmount
            }
        }
    ) {
        // Always display exactly 6 weeks (6 rows)
        repeat(6) { weekIndex ->
            // Calculate the start date for each week directly (no mutable state)
            val weekStartDate = startDayOfWeek.plusDays((weekIndex * 7).toLong())

            // Create a WeekRow Composable for each week
            WeekRow(
                startDate = weekStartDate,
                daysInWeek = daysInWeek,
                yearMonth = yearMonth,
                allEvents = events
            )
        }
    }
}

/**
 * This Composable function represents a row in the calendar grid that displays the dates for a week.
 * It observes the current calendar mode (Gregorian or Jalali) from the ViewModel and generates a row of dates for the week.
 * Each date in the row is a Composable function that represents a day in the calendar.
 *
 * @param startDate The start date of the week.
 * @param daysInWeek The number of days in a week.
 * @param yearMonth The YearMonth of the current month.
 */
@Composable
fun WeekRow(
    startDate: LocalDate,
    daysInWeek: Int,
    yearMonth: YearMonth,
    allEvents: List<Event>
) {
    // Create a Row Composable for the week
    Row(
        // Arrange the children of the Row horizontally with space between them
        horizontalArrangement = Arrangement.SpaceBetween,
        // Align the children of the Row vertically in the center
        verticalAlignment = Alignment.CenterVertically,
        // Apply a Modifier to the Row to fill the maximum width and add padding
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 0.dp)
    ) {
        // Loop through each day in the week
        for (day in 1..daysInWeek) {
            // Calculate the current date for this day
            val currentDate = startDate.plusDays((day - 1).toLong())

            // Create a DayBox Composable for each day
            com.cocode.calendar.components.cells.DayBox(
                currentDate = currentDate,
                isInCurrentMonth = currentDate.month == yearMonth.month,
                events = allEvents.filter { event -> event.occursOn(currentDate) }
            )
        }
    }
}
