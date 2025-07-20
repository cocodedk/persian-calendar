package com.cocode.calendar

import CalendarConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import utils.DateTimeUtils
import utils.Strings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

/**
 * This Composable function represents the header of the calendar application.
 * It displays the current month and year, and buttons to navigate to the next and previous months.
 * The displayed month and year can be in either Gregorian or Persian (Jalali) format, depending on the current calendar mode.
 *
 * @Composable This annotation indicates that this function is a Composable function
 * in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarHeader() {
    val viewModel: CalendarViewModel = viewModel()
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    val (primaryText, secondaryText) = remember(gregorianDate, isJalaliCalendar) {
        val jalaliMonths = CalendarConverter.gregorianToJalaliMonths(gregorianDate)
        val jalaliDate = CalendarConverter.gregorianToJalali(gregorianDate)
        val jalaliWeekNumber = CalendarConverter.getJalaliWeekNumber(jalaliDate)

        val jalaliText = "week $jalaliWeekNumber - ${jalaliMonths["left"]?.monthName} - ${jalaliMonths["right"]?.monthName} ${jalaliMonths["right"]?.year}"
        val gregorianText = "${gregorianDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))} - week ${DateTimeUtils.getCurrentWeekNumber(gregorianDate)}"

        if (isJalaliCalendar) jalaliText to gregorianText else gregorianText to jalaliText
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = primaryText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = CalColors.active_text
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = secondaryText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = CalColors.inactive_text,
            fontSize = 12.sp
        )
    }
}

/**
 * This Composable function displays the current time in Iran.
 *
 * It first creates a mutable state to hold the current time. This state is remembered across recompositions.
 * Then, it launches a coroutine that continuously updates the current time every second.
 * The current time is retrieved by calling the getCurrentTimeInIran function, which returns the current date and time in Iran.
 * The retrieved time is then formatted to a string of "HH:mm:ss Z" and stored in the mutable state.
 * Finally, it displays the current time in a Text Composable that is centered in a Box Composable.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun DisplayTimeInIran() {

    // This composable function displays the current time in Iran.
    // This state is remembered across recompositions
    val currentTime = remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = Unit){
        while (currentCoroutineContext().isActive){
            val iranTime = DateTimeUtils.getCurrentTimeInIran()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val formattedIranTime = iranTime.format(formatter)
            currentTime.value = formattedIranTime
            delay(1000)
        }
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        // put the text in a row and center it
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Iran time: ${currentTime.value}",
                style = MaterialTheme.typography.bodyLarge,
                color = CalColors.text
            )
        }
    }

}

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
                yearMonth = yearMonth
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
    yearMonth: YearMonth
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

            // Create a DayBox Composable for each day
            DayBox(
                currentDate = currentDate,
                jalaliDate = jalaliDate,
                isInCurrentMonth = currentDate.month == yearMonth.month
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
            .clickable(enabled = isInCurrentMonth) {} // Add a click listener to the box
    ) {
        // Create a Text Composable to display the date number
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = fontColor,
            fontWeight = FontWeight.Bold,
            textDecoration = if (isCurrentDay) androidx.compose.ui.text.style.TextDecoration.Underline else null
        )
    }
}

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

/**
 * Month and Year navigation buttons that open selection dialogs.
 * Replaces the old CrossClickArea navigation system.
 */
@Composable
fun CalendarNavigation() {
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()

    // Don't show navigation when converter is visible
    if (showConverter) {
        return
    }

    // State for dialog visibility
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Month Selection Button
        Button(
            onClick = { showMonthPicker = true },
            colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .height(48.dp)
        ) {
            Text(
                text = "Select Month",
                color = CalColors.text,
                fontWeight = FontWeight.Bold
            )
        }

        // Year Selection Button
        Button(
            onClick = { showYearPicker = true },
            colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .height(48.dp)
        ) {
            Text(
                text = "Select Year",
                color = CalColors.text,
                fontWeight = FontWeight.Bold
            )
        }
    }

        // Current date for month selection
    val currentDate by viewModel.gregorianDate.observeAsState(LocalDate.now())

    // Show dialogs when requested
    if (showMonthPicker) {
        MonthPickerDialog(
            onDismiss = { showMonthPicker = false },
            onMonthSelected = { month ->
                val newYearMonth = YearMonth.of(currentDate?.year ?: LocalDate.now().year, month)
                viewModel.changeMonth(newYearMonth)
                showMonthPicker = false
            }
        )
    }

    if (showYearPicker) {
        YearPickerDialog(
            onDismiss = { showYearPicker = false },
            onYearSelected = { year ->
                viewModel.changeYear(year)
                showYearPicker = false
            }
        )
    }
}

/**
 * Dialog for selecting a month from a 3x4 grid layout.
 */
@Composable
fun MonthPickerDialog(
    onDismiss: () -> Unit,
    onMonthSelected: (Int) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val currentDate by viewModel.gregorianDate.observeAsState(LocalDate.now())
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(false)

    // Use centralized month names from Strings object
    val months = if (isJalaliCalendar) Strings.Months.JALALI_ABBREVIATED else Strings.Months.GREGORIAN_ABBREVIATED
    val currentMonth = if (isJalaliCalendar) {
        // Convert current Gregorian date to Jalali and get the Jalali month
        currentDate?.let { CalendarConverter.gregorianToJalali(it).monthValue } ?: 1
    } else {
        currentDate?.monthValue ?: 1
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Select Month",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CalColors.active_text,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 3x4 grid of months
            repeat(4) { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(3) { col ->
                        val monthIndex = row * 3 + col
                        if (monthIndex < 12) {
                            val isSelected = monthIndex + 1 == currentMonth
                            Button(
                                onClick = { onMonthSelected(monthIndex + 1) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected)
                                        CalColors.active_button_background
                                    else CalColors.button_background
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = months[monthIndex],
                                    color = CalColors.text,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Cancel button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Dialog for selecting a year from a scrollable list.
 */
@Composable
fun YearPickerDialog(
    onDismiss: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val currentDate by viewModel.gregorianDate.observeAsState(LocalDate.now())
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(false)

    val currentGregorianYear = currentDate?.year ?: LocalDate.now().year

    // Year range (can be adjusted as needed)
    val startGregorianYear = 1900
    val endGregorianYear = 2100
    val gregorianYears = (startGregorianYear..endGregorianYear).toList()

    // Convert to display years and current year based on calendar mode
    val (displayYears, currentDisplayYear) = if (isJalaliCalendar) {
        // Convert Gregorian years to Jalali years for display
        val jalaliYears = gregorianYears.map { gregorianYear ->
            val tempDate = LocalDate.of(gregorianYear, 6, 15) // Use middle of year for conversion
            CalendarConverter.gregorianToJalali(tempDate).year
        }

        // Get current Jalali year
        val currentJalaliYear = CalendarConverter.gregorianToJalali(currentDate ?: LocalDate.now()).year

        jalaliYears to currentJalaliYear
    } else {
        gregorianYears to currentGregorianYear
    }

    // Calculate the index of the current year in the display list
    val currentYearIndex = displayYears.indexOf(currentDisplayYear)

    // Remember the LazyColumn state for scrolling control
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Auto-scroll to current year when dialog opens
    LaunchedEffect(Unit) {
        if (currentYearIndex >= 0) {
            // Scroll to current year, centering it in the visible area
            listState.scrollToItem(
                index = maxOf(0, currentYearIndex - 2) // Show current year with some context above
            )
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
                .height(400.dp)
        ) {
            Text(
                text = "Select Year",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CalColors.active_text,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Scrollable list of years
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(displayYears.indices.toList()) { index ->
                    val displayYear = displayYears[index]
                    val gregorianYear = gregorianYears[index]
                    val isSelected = displayYear == currentDisplayYear

                    Button(
                        onClick = {
                            // Always pass the Gregorian year to maintain internal consistency
                            onYearSelected(gregorianYear)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                CalColors.active_button_background
                            else CalColors.button_background
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .height(52.dp)
                    ) {
                        Text(
                            text = displayYear.toString(),
                            color = CalColors.text,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // Cancel button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
