package com.cocode.calendar

import CalendarConverter
// import android.util.Log
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cocode.calendar.ui.theme.CalendarTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.TextField
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import utils.DateTimeUtils


// Describe the application
// This is a simple calendar app that displays a calendar view
// with the ability to toggle between Gregorian and Persian (Jalali) calendars.


/**
 * This is the main activity for the calendar application.
 *
 * @property MainActivity This class extends ComponentActivity, which is a base class for activities that use the new androidx APIs.
 * @constructor Creates an instance of MainActivity.
 */
class MainActivity : ComponentActivity() {

    /**
     * This function is called when the activity is starting.
     * This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply the theme for the calendar application
            CalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CalColors.background

                ) {
                    // Call the main screen composable of the calendar application
                    CalendarApp()
                }
            }
        }
    }
}


/**
 * This class represents the ViewModel for the Calendar application.
 * It holds the state for the current Gregorian date and the calendar mode (Gregorian or Jalali).
 *
 * @property CalendarViewModel This class extends ViewModel, which is designed to store and manage
 * UI-related data in a lifecycle conscious way.
 * @constructor Creates an instance of CalendarViewModel.
 */
class CalendarViewModel : ViewModel() {
    // MutableStateFlow to hold the current Gregorian date
    private val _gregorianDate = MutableStateFlow(LocalDate.now())
    // MutableStateFlow to hold the current calendar mode (false for Gregorian, true for Jalali)
    private val _isJalaliCalendar = MutableStateFlow(false)

    // Expose an immutable LiveData for observers to observe the current Gregorian date
    val gregorianDate = _gregorianDate.asLiveData()
    // Expose an immutable LiveData for observers to observe the current calendar mode
    val isJalaliCalendar = _isJalaliCalendar.asLiveData()

    /**
     * Updates the current Gregorian date.
     *
     * @param newDate The new Gregorian date to set.
     */
    fun updateGregorianDate(newDate: LocalDate) {
        _gregorianDate.value = newDate
    }

    /**
     * Toggles the current calendar mode.
     * If the current mode is Gregorian, it changes to Jalali, and vice versa.
     */
    fun toggleIsJalaliCalendar() {
        _isJalaliCalendar.value = !_isJalaliCalendar.value
    }

    /**
     * Changes the current month in the calendar.
     *
     * This function updates the current Gregorian date to either the current date (if the new month
     * is the current month) or the first day of the new month. It ensures that when changing to
     * the current month, the date is set to today's date rather than the first day of the month.
     *
     * @param newYearMonth The new year and month to set the calendar to. This parameter
     *                     determines which month the calendar will display.
     */
    fun changeMonth(newYearMonth: YearMonth) {
        val now = LocalDate.now()
        _gregorianDate.value = if (newYearMonth.year == now.year && newYearMonth.monthValue == now.monthValue) {
            now
        } else {
            newYearMonth.atDay(1)
        }
    }

    /**
     * Changes the year of the current Gregorian date.
     *
     * This function updates the current Gregorian date to the first day of the specified year.
     * It maintains the current month but resets the day to the first of that month.
     *
     * @param newYear The year to set the current date to.
     */
    fun changeYear(newYear: Int) {
        _gregorianDate.value = _gregorianDate.value.withYear(newYear).withDayOfMonth(1)
    }
}


/**
 * This Composable function represents the main application for the calendar.
 *
 * @RequiresApi(Build.VERSION_CODES.O) This annotation indicates that this function requires API level 26 (Android 8.0, Oreo) or higher.
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarApp() {
    val viewModel: CalendarViewModel = viewModel()
    viewModel.isJalaliCalendar.observeAsState(initial = false)
    viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    CalendarScreen()
}


/**
 * This Composable function represents the main screen of the calendar application.
 * @RequiresApi(Build.VERSION_CODES.O) This annotation indicates that this function
 * requires API level 26 (Android 8.0, Oreo) or higher.
 * @Composable This annotation indicates that this function is a Composable function
 * in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarScreen() {

    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())
    // this state variable controls the visibility of the converter
    var showConverter by remember { mutableStateOf(false) }
    
    // This Composable function is the main screen of the app.
    Column {
        // The header of the calendar view, which includes the current month and year,
        // and buttons to navigate to the next and previous months.
        CalendarHeader()

        // A Composable function that displays the current time in Iran.
        DisplayTimeInIran()

        // The header of the calendar view that displays the days of the week.
        WeekDaysHeader()

        // The grid of the calendar view that displays the dates.
        CalendarGrid()

        // The controls for the calendar view, including a button to navigate to today's date
        CalControls()

        Spacer(modifier = Modifier.height(1.dp))

        // Add a button to toggle the visibility of the converter
        Button(
            onClick = { showConverter = !showConverter },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background)
        ) {
            Text(
                text = if (showConverter) "Hide Converter" else "Show Converter",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }

        // Conditionally render the converter based on the showConverter state
        if (showConverter) {
            JalaliToGregorianConverter()
        }

        Spacer(modifier = Modifier.weight(1f))

        CrossClickArea(
            onClickRight = { viewModel.changeMonth(YearMonth.from(gregorianDate).plusMonths(1)) },
            onClickLeft = { viewModel.changeMonth(YearMonth.from(gregorianDate).minusMonths(1)) },
            onClickUp = { viewModel.changeYear(gregorianDate.year + 1) },
            onClickDown = { viewModel.changeYear(gregorianDate.year - 1) },
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
        )
    }
}


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
 * Displays a box representing a day in the calendar.
 *
 * This composable function creates a box for a specific date, which can be either in Gregorian
 * or Jalali calendar format. The box's appearance (background color and text color) is determined
 * based on whether the date is in the current month, is today's date, or belongs to the previous
 * or next month.
 *
 * @param currentDate The date to be displayed in the box (in Gregorian format).
 * @param isInCurrentMonth Boolean indicating whether this date is part of the currently displayed month.
 * @param jalaliDate The Jalali (Persian) equivalent of the currentDate.
 * @param viewModel The [CalendarViewModel] used to access calendar state and operations. Defaults to the current ViewModel.
 */
@Composable
fun DayBox(
    currentDate: LocalDate,
    isInCurrentMonth: Boolean,
    jalaliDate: CalendarConverter.Companion.JalaliDate,
    viewModel: CalendarViewModel = viewModel()
) {
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    val convertedGregorianDate = remember(gregorianDate) {
        CalendarConverter.gregorianToJalali(gregorianDate)
    }

    val (backgroundColor, fontColor) = remember(isJalaliCalendar, currentDate, gregorianDate, jalaliDate, convertedGregorianDate, isInCurrentMonth) {
        calculateColors(isJalaliCalendar, currentDate, gregorianDate, jalaliDate, convertedGregorianDate, isInCurrentMonth)
    }

    val text = if (isJalaliCalendar) jalaliDate.dayOfMonth.toString() else currentDate.dayOfMonth.toString()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 55.dp, height = 60.dp)
            .background(backgroundColor)
            .border(width = 0.dp, color = CalColors.day_border)
            .clickable(enabled = isInCurrentMonth) {}
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = fontColor,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Calculates the background and text colors for a calendar day based on the current calendar system and date.
 *
 * @param isJalaliCalendar Boolean indicating whether the Jalali (Persian) calendar is in use.
 * @param currentDate The date for which colors are being calculated.
 * @param gregorianDate The current Gregorian date being displayed in the calendar.
 * @param jalaliDate The Jalali equivalent of the current date.
 * @param convertedGregorianDate The Jalali equivalent of the gregorianDate.
 * @param isInCurrentMonth Boolean indicating whether the currentDate is in the currently displayed month.
 * @return A Pair of Color objects, where the first Color is the background color and the second is the text color.
 */
private fun calculateColors(
    isJalaliCalendar: Boolean,
    currentDate: LocalDate,
    gregorianDate: LocalDate,
    jalaliDate: CalendarConverter.Companion.JalaliDate,
    convertedGregorianDate: CalendarConverter.Companion.JalaliDate,
    isInCurrentMonth: Boolean
): Pair<Color, Color> {
    if (isJalaliCalendar) {
        return calculateJalaliColors(currentDate, jalaliDate, convertedGregorianDate)
    }
    return calculateGregorianColors(currentDate, gregorianDate, isInCurrentMonth)
}

/**
 * Calculates the background and text colors for a day in the Jalali calendar.
 *
 * This function determines the appropriate colors for a calendar day based on its relationship
 * to the current date and the displayed month in the Jalali calendar system.
 *
 * @param currentDate The Gregorian date being evaluated.
 * @param jalaliDate The Jalali (Persian) equivalent of the current date.
 * @param convertedGregorianDate The Jalali equivalent of the currently displayed Gregorian date in the calendar.
 * @return A Pair of Color objects, where the first Color is the background color and the second is the text color.
 */
private fun calculateJalaliColors(
    currentDate: LocalDate,
    jalaliDate: CalendarConverter.Companion.JalaliDate,
    convertedGregorianDate: CalendarConverter.Companion.JalaliDate
): Pair<Color, Color> {
    val adjustedDate = DateTimeUtils.adjustDateForDeviceTimeZone()
    when {
        currentDate.isEqual(adjustedDate) ->
            return Pair(CalColors.current_day_background, CalColors.current_day_text)
        isJalaliDateAfter(jalaliDate, convertedGregorianDate) ->
            return Pair(CalColors.next_month_background, CalColors.next_month_text)
        isJalaliDateBefore(jalaliDate, convertedGregorianDate) ->
            return Pair(CalColors.prev_month_background, CalColors.prev_month_text)
        else -> return Pair(Color.White, Color.Black)
    }
}


/**
 * Calculates the background and text colors for a day in the Gregorian calendar.
 *
 * This function determines the appropriate colors for a calendar day based on its relationship
 * to the current date and the displayed month in the Gregorian calendar system.
 *
 * @param currentDate The date being evaluated for color assignment.
 * @param gregorianDate The current Gregorian date being displayed in the calendar.
 * @param isInCurrentMonth Boolean indicating whether the [currentDate] is in the currently displayed month.
 * @return A Pair of Color objects, where the first Color is the background color and the second is the text color.
 */
private fun calculateGregorianColors(
    currentDate: LocalDate,
    gregorianDate: LocalDate,
    isInCurrentMonth: Boolean
): Pair<Color, Color> {
    when {
        !isInCurrentMonth && currentDate.isBefore(gregorianDate) ->
            return Pair(CalColors.prev_month_background, CalColors.prev_month_text)
        !isInCurrentMonth && currentDate.isAfter(gregorianDate) ->
            return Pair(CalColors.next_month_background, CalColors.next_month_text)
        currentDate.isEqual(LocalDate.now()) ->
            return Pair(CalColors.current_day_background, CalColors.current_day_text)
        else -> return Pair(Color.White, Color.Black)
    }
}


/**
 * Determines if a given Jalali date is after another Jalali date.
 *
 * This function compares two Jalali dates to check if the first date is chronologically
 * after the second date. The comparison is based on both the year and month values.
 *
 * @param jalaliDate The Jalali date to be checked.
 * @param convertedGregorianDate The Jalali date to compare against, typically converted from a Gregorian date.
 * @return Boolean True if [jalaliDate] is after [convertedGregorianDate], false otherwise.
 */
private fun isJalaliDateAfter(
    jalaliDate: CalendarConverter.Companion.JalaliDate,
    convertedGregorianDate: CalendarConverter.Companion.JalaliDate
): Boolean {
    return (jalaliDate.monthValue > convertedGregorianDate.monthValue && jalaliDate.year >= convertedGregorianDate.year)
            || (jalaliDate.year > convertedGregorianDate.year)
}


/**
 * Determines if a given Jalali date is before another Jalali date.
 *
 * This function compares two Jalali dates to check if the first date is chronologically
 * before the second date. The comparison is based on both the year and month values.
 *
 * @param jalaliDate The Jalali date to be checked.
 * @param convertedGregorianDate The Jalali date to compare against, typically converted from a Gregorian date.
 * @return Boolean True if [jalaliDate] is before [convertedGregorianDate], false otherwise.
 */
private fun isJalaliDateBefore(
    jalaliDate: CalendarConverter.Companion.JalaliDate,
    convertedGregorianDate: CalendarConverter.Companion.JalaliDate
): Boolean {
    return (jalaliDate.monthValue < convertedGregorianDate.monthValue)
            || (jalaliDate.year < convertedGregorianDate.year)
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
    val daysOfWeek = remember { listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat") }

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
        val color = if(day != "Sun" && day != "Sat") CalColors.weekday_text else CalColors.weekend_text
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
    // Get the first and last day of the current month
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    // Determine the start and end day for the calendar grid to include days from the previous month and the next month
    val startDayOfWeek = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val endDayOfWeek = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

    // Initialize the current day to the start day
    var currentDay = startDayOfWeek

    // Create a Column Composable for the calendar grid
    Column {
        // Loop through each week in the month
        while (currentDay <= endDayOfWeek) {
            // Create a WeekRow Composable for each week
            WeekRow(
                startDate = currentDay,
                daysInWeek = daysInWeek,
                yearMonth = yearMonth
            ) {
                // Update the current day to the start day of the next week
                currentDay = currentDay.plusDays(7)
            }
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
 * @param updateDay A lambda function that is invoked to update the current day to the start day of the next week.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun WeekRow(
    startDate: LocalDate,
    daysInWeek: Int,
    yearMonth: YearMonth,
    updateDay: (LocalDate) -> Unit
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
        // Initialize the current date to the start date
        var currentDate = startDate
        // Loop through each day in the week
        for (day in 1..daysInWeek) {
            val jalaliDate = CalendarConverter.gregorianToJalali(currentDate)
            // Create a DayBox Composable for each day
            DayBox(
                currentDate = currentDate,
                jalaliDate = jalaliDate,
                isInCurrentMonth = currentDate.month == yearMonth.month
            )
            // Update the current date to the next day
            currentDate = currentDate.plusDays(1)
        }
        // Update the current day to the start day of the next week
        updateDay(currentDate)
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

    // Create a Box Composable for the date
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(55.dp)
            .height(60.dp)
            .background(backgroundColor)
            .border(width = 0.dp, color = CalColors.day_border)
            .clickable(enabled = isInCurrentMonth) {} // Add a click listener to the box
    ) {
        // Create a Text Composable to display the date number
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = fontColor,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun CalControls() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(left = 4.dp, top = 1.dp, right = 4.dp, bottom = 0.dp)
    ) {
        TodayButton()
        ToggleCalendarButton()
    }
}


@Composable
fun CrossClickArea(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    onClickUp: () -> Unit,
    onClickDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            InactiveCell(width = 0.2f)
            ClickableCell(
                onClick = onClickUp, onLongPress = onClickUp, width = 0.75f, Icons.Default.KeyboardArrowUp, contentDescription = "Next year")
            InactiveCell(width = 1f)
        }

        Text("+Y", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = CalColors.text, fontSize = 12.sp)

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()

        ) {
            ClickableCell(
                onClick = onClickLeft, onLongPress = onClickLeft, width = 0.2f, Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Month")
            Box(modifier = Modifier.height(50.dp), contentAlignment = Alignment.Center) {
                Text("-M",
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = CalColors.text,
                    fontSize = 12.sp
                )
            }
            InactiveCell(width = 0.67f)  // Optionally, this cell can be interactive or display info.
            Box(modifier = Modifier.height(50.dp), contentAlignment = Alignment.Center) {
                Text(
                    "+M",
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = CalColors.text,
                    fontSize = 12.sp
                )
            }
            ClickableCell(
                onClick = onClickRight, onLongPress = onClickRight, width = 1f, Icons.Default.KeyboardArrowRight, contentDescription = "Next Month")

        }

        Text("-Y", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = CalColors.text, fontSize = 12.sp)

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            InactiveCell(0.2f)
            ClickableCell(onClick = onClickDown, onLongPress = onClickDown, width = 0.75f, Icons.Default.KeyboardArrowDown, contentDescription = "Next year")
            InactiveCell(1f)
        }
    }
}



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClickableCell(onClick: () -> Unit, onLongPress: () -> Unit, width: Float= 0.5f, icon: ImageVector, contentDescription: String? = null) {
    var holding by remember { mutableStateOf(false) }
    LaunchedEffect(holding) {
        while (holding) {
            onLongPress()
            delay(300) // Delay between repeated actions, adjust as necessary
        }
    }
    Icon(
        icon,
        contentDescription = contentDescription,
        tint = CalColors.text,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(width)
            //.border(1.dp, Color.Red)
            //.background(Color.LightGray)
            .pointerInteropFilter {
                when (it.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        holding = true
                        onClick() // Also trigger onClick at the start
                    }

                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        holding = false
                    }
                }
                true
            }
    )
}


@Composable
fun InactiveCell(width: Float = 0.5f) {
    Box(
        modifier = Modifier
            .fillMaxWidth(width)
            .height(50.dp)
            //.border(1.dp, Color.Cyan)
            //.background(Color.Gray)
    )
}


/**
 * This Composable function represents a button that toggles
 * the calendar view between Gregorian and Persian (Jalali) modes.
 *
 * @Composable This annotation indicates that this function is a
 * Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun ToggleCalendarButton() {

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
                text = if (isJalaliCalendar) "Gregorian" else "Persian",
                color = CalColors.text,
                fontWeight = FontWeight.Bold

            )
        }
    }
}


/**
 * This Composable function represents a button that updates the current date to today's date.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
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
                .width(193.dp)
                .height(52.dp)
        ) {
            // Set the display text for the button
            Text(
                text = "Today",
                color = CalColors.text,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

/**
 * Jalali to Gregorian Date Convertor
 */

@Composable
fun JalaliToGregorianConverter() {
    var jalaliYear by remember { mutableStateOf("") }
    var jalaliMonth by remember { mutableStateOf("") }
    var jalaliDay by remember { mutableStateOf("") }
    var gregorianDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Convert Jalali to Gregorian",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = jalaliYear,
                onValueChange = { jalaliYear = it },
                label = { Text("Year") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = jalaliMonth,
                onValueChange = { jalaliMonth = it },
                label = { Text("Month") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = jalaliDay,
                onValueChange = { jalaliDay = it },
                label = { Text("Day") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    val year = jalaliYear.toInt()
                    val month = jalaliMonth.toInt()
                    val day = jalaliDay.toInt()
                    gregorianDate = CalendarConverter.jalaliToGregorian(year, month, day)
                } catch (e: Exception) {
                    gregorianDate = null
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Convert")
        }

        gregorianDate?.let {
            Text(
                "Gregorian Date: ${it.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        } ?: run {
            Text(
                "Enter a valid Jalali date",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red
            )
        }
    }
}