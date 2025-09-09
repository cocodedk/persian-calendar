package com.cocode.calendar.components

import CalendarConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.Event
import utils.DateTimeUtils
import utils.Strings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

/**
 * Displays a header row containing the days of the week.
 *
 * This composable function creates a horizontal row that shows the abbreviated names
 * of the days of the week (Sun, Mon, Tue, etc.). The row is styled with a border
 * and rounded corners at the top.
 *
 * The function uses [remember] to memorize the list of day names, preventing
 * unnecessary recomposition.
 *
 * @see DayOfWeekBox
 */
@Composable
fun WeekDaysHeader() {
    val daysOfWeek = remember { Strings.Calendar.DAYS_OF_WEEK }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 0.dp)
            .border(
                width = 1.dp,
                color = CalColors.day_background,
                RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)
            )
    ) {
        daysOfWeek.forEach { day ->
            DayOfWeekBox(day)
        }
    }
}

/**
 * Displays a box containing the day of the week.
 *
 * This composable function creates a box with centered text representing a day of the week.
 * The text color is different for weekdays and weekends.
 *
 * @param day The string representation of the day of the week (e.g., "Mon", "Tue").
 */
@Composable
fun DayOfWeekBox(day: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(55.dp)
            .height(40.dp)
    ) {
        val color = if(day != Strings.Calendar.SUN && day != Strings.Calendar.SAT) CalColors.weekday_text else CalColors.weekend_text
        Text(
            text = day,
            color = color
        )
    }
}

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
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarGrid() {
    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    // Observe the gregorianDate LiveData from the ViewModel
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())
    // Observe events from the ViewModel
    val events by viewModel.events.collectAsState()

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
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun WeekRow(
    startDate: LocalDate,
    daysInWeek: Int,
    yearMonth: YearMonth,
    allEvents: List<Event> = emptyList()
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
            val jalaliDate = CalendarConverter.gregorianToJalali(currentDate)

            // Filter events for this specific date
            val dayEvents = allEvents.filter { event -> event.occursOn(currentDate) }

            // Create a DayBox Composable for each day
            DayBox(
                currentDate = currentDate,
                jalaliDate = jalaliDate,
                isInCurrentMonth = currentDate.month == yearMonth.month,
                events = dayEvents
            )
        }
    }
}

/**
 * This Composable function represents a box that displays a date in the calendar grid.
 * It observes the current calendar mode (Gregorian or Jalali) from the ViewModel and generates a box for the date.
 * The box includes the date number and is colored based on certain conditions.
 *
 * @param currentDate The current date that the box represents.
 * @param isInCurrentMonth A boolean indicating whether the date is in the current month.
 * @param jalaliDate The Jalali date that the box represents, if the current calendar mode is Jalali.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun DayBox(
    currentDate: LocalDate,
    isInCurrentMonth: Boolean,
    jalaliDate: CalendarConverter.Companion.JalaliDate,
    events: List<Event> = emptyList()
) {
    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    // Observe the isJalaliCalendar LiveData from the ViewModel
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)
    // Observe the gregorianDate LiveData from the ViewModel
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    // Determine the background color and font color of the box based on certain conditions

    val convertedGregorianDate = CalendarConverter.gregorianToJalali(gregorianDate)

    val backgroundColor = when {
        !isJalaliCalendar && !isInCurrentMonth && currentDate.isBefore(gregorianDate) -> {
            CalColors.prev_month_background
        }
        !isJalaliCalendar && !isInCurrentMonth && currentDate.isAfter(gregorianDate) -> {
            CalColors.next_month_background
        }
        !isJalaliCalendar && currentDate.isEqual(LocalDate.now()) -> {
            CalColors.current_day_background
        }
        isJalaliCalendar && currentDate.isEqual(DateTimeUtils.adjustDateForDeviceTimeZone()) -> {
            CalColors.current_day_background
        }
        (isJalaliCalendar && ((jalaliDate.monthValue > convertedGregorianDate.monthValue && jalaliDate.year >= convertedGregorianDate.year)
                || (jalaliDate.year > convertedGregorianDate.year))) -> {
            CalColors.next_month_background
                }
        (isJalaliCalendar && ((jalaliDate.monthValue < convertedGregorianDate.monthValue)
                || (jalaliDate.year < convertedGregorianDate.year))) -> {
            CalColors.prev_month_background
                }
        else -> {
            Color.White
        }
    }

    val fontColor = when {
        !isJalaliCalendar && !isInCurrentMonth && currentDate.isBefore(gregorianDate) -> {
            CalColors.prev_month_text
        }
        !isJalaliCalendar && !isInCurrentMonth && currentDate.isAfter(gregorianDate) -> {
            CalColors.next_month_text
        }
        !isJalaliCalendar && currentDate.isEqual(LocalDate.now()) -> {
            CalColors.current_day_text
        }
        isJalaliCalendar && currentDate.isEqual(DateTimeUtils.adjustDateForDeviceTimeZone()) -> {
            CalColors.current_day_text
        }
        (isJalaliCalendar && ((jalaliDate.monthValue > convertedGregorianDate.monthValue && jalaliDate.year >= convertedGregorianDate.year)
                || (jalaliDate.year > convertedGregorianDate.year))) -> {
            CalColors.next_month_text
                }
        (isJalaliCalendar && ((jalaliDate.monthValue < convertedGregorianDate.monthValue)
                || (jalaliDate.year < convertedGregorianDate.year))) -> {
            CalColors.prev_month_text
                }
        else -> {
            Color.Black
        }
    }

    // Determine the text to display in the box based on the current calendar mode
    val text = if(isJalaliCalendar) {
        jalaliDate.dayOfMonth.toString()
    } else {
        currentDate.dayOfMonth.toString()
    }

    // Check if this is the current day for 3D border effect
    val isCurrentDay = if (isJalaliCalendar) {
        currentDate.isEqual(DateTimeUtils.adjustDateForDeviceTimeZone())
    } else {
        currentDate.isEqual(LocalDate.now())
    }

        // Create a Box Composable for the date with 3D border effect for current day
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(55.dp)
            .height(60.dp)
            .background(backgroundColor)
            .then(
                if (isCurrentDay) {
                    // 3D border effect for current day
                    Modifier
                        .border(
                            width = 2.dp,
                            color = Color(0xFF4CAF50), // Main green border
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(1.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF81C784), // Lighter green inner border for 3D effect
                            shape = RoundedCornerShape(3.dp)
                        )
                } else {
                    Modifier.border(width = 0.dp, color = CalColors.day_border)
                }
            )
            .clickable(enabled = isInCurrentMonth) {
                if (events.isNotEmpty()) {
                    viewModel.showEventListDialog(currentDate)
                } else {
                    viewModel.showEventCreationDialog(currentDate)
                }
            } // Add a click listener to the box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Create a Text Composable to display the date number
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = fontColor,
                fontWeight = FontWeight.Bold,
                textDecoration = if (isCurrentDay) androidx.compose.ui.text.style.TextDecoration.Underline else null
            )

            // Show event indicator if there are events on this date
            if (events.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = Color(0xFF2196F3), // Blue dot for events
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
}
