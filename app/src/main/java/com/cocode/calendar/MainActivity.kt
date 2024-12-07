package com.cocode.calendar

import CalendarConverter
// import android.util.Log
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    
    private val _showConverter = MutableStateFlow(false)
    val showConverter: StateFlow<Boolean> = _showConverter.asStateFlow()

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
    
    fun toggleConverter() {
        _showConverter.value =!_showConverter.value
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

        CalendarConverterBox()

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
                text = "Today",
                color = CalColors.text,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

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
                style = MaterialTheme.typography.bodyLarge,
                color = CalColors.text
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
                text = if (isJalaliCalendar) "Gregorian" else "Jalali",
                color = CalColors.text,
                fontWeight = FontWeight.Bold

            )
        }
    }
}


@Composable
fun CalendarConverterBox() {

    JalaliToGregorianConverter()
    GregorianToJalaliConverter()
}


/**
 * Creates a composable cross-shaped click area with directional controls.
 *
 * This function generates a layout with clickable areas for up, down, left, and right directions,
 * along with year and month indicators. It's designed to provide intuitive navigation controls
 * for a calendar or similar date-based interface.
 *
 * @param onClickLeft Lambda to be invoked when the left arrow is clicked or long-pressed.
 * @param onClickRight Lambda to be invoked when the right arrow is clicked or long-pressed.
 * @param onClickUp Lambda to be invoked when the up arrow is clicked or long-pressed.
 * @param onClickDown Lambda to be invoked when the down arrow is clicked or long-pressed.
 * @param modifier Modifier to be applied to the main column layout of this composable.
 *
 * @return A composable that displays a cross-shaped click area with directional controls.
 */
@Composable
fun CrossClickArea(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    onClickUp: () -> Unit,
    onClickDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()

    Log.d("CrossClickArea", "showConverter: $showConverter")

    if (showConverter){
        return
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            SpacerCell(width = 0.2f)
            ClickableCell(
                onClick = onClickUp, onLongPress = onClickUp, width = 0.75f,
                icon = Icons.Default.KeyboardArrowUp, contentDescription = "Next year")
            SpacerCell(width = 1f)
        }

        Text("+Y", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, 
            fontWeight = FontWeight.Bold, color = CalColors.text, fontSize = 12.sp)

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()

        ) {
            ClickableCell(
                onClick = onClickLeft, 
                onLongPress = onClickLeft, 
                width = 0.2f,
                icon = Icons.Default.KeyboardArrowLeft, 
                contentDescription = "Previous Month"
            )
            CenteredText("-M")
            SpacerCell(width = 0.67f)  // Optionally, this cell can be interactive or display info.
            CenteredText("+M")
            ClickableCell(
                onClick = onClickRight, onLongPress = onClickRight, width = 1f,
                icon = Icons.Default.KeyboardArrowRight, contentDescription = "Next Month"
            )

        }

        Text("-Y", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, 
            fontWeight = FontWeight.Bold, color = CalColors.text, fontSize = 12.sp)

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            SpacerCell(0.2f)
            ClickableCell(onClick = onClickDown, onLongPress = onClickDown, width = 0.75f,
                icon = Icons.Default.KeyboardArrowDown, contentDescription = "Next year")
            SpacerCell(1f)
        }
    }
}



/**
 * Creates a clickable cell with an icon that responds to both click and long-press events.
 *
 * This composable function creates an interactive cell with an icon that can be clicked or long-pressed.
 * It supports continuous action on long-press, repeating the action at regular intervals.
 *
 * @param onClick A lambda function that is called when the cell is clicked.
 * @param onLongPress A lambda function that is called repeatedly while the cell is being long-pressed.
 * @param width The width of the cell as a fraction of its parent's width. Defaults to 0.5f.
 * @param icon The [ImageVector] to be displayed in the cell.
 * @param contentDescription An optional string describing the icon for accessibility purposes.
 *
 * @OptIn(ExperimentalComposeUiApi::class) This function uses experimental Compose UI APIs.
 * @Composable This function is a Jetpack Compose composable.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClickableCell(
    onClick: () -> Unit, 
    onLongPress: () -> Unit, 
    width: Float= 0.5f, 
    icon: ImageVector, 
    contentDescription: String? = null
) {
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


/**
 * Creates a spacer cell in a Compose layout.
 *
 * This composable function generates an empty Box that acts as a spacer
 * in the layout. It's useful for creating gaps or empty spaces between
 * other composable.
 *
 * @param width The width of the spacer as a fraction of its parent's width.
 *              Defaults to 0.5f (50% of the parent's width).
 * @return A composable [Box] that serves as a spacer in the layout.
 */
@Composable
fun SpacerCell(width: Float = 0.5f) {
    Box(
        modifier = Modifier
            .fillMaxWidth(width)
            .height(50.dp)
    )
}


@Composable
fun CenteredText(
    text: String,
    fontSize: TextUnit = 12.sp,
    contentAlignment: Alignment = Alignment.Center
) {
    Box(
        modifier = Modifier.height(50.dp),
        contentAlignment = contentAlignment
    ) {
        Text(
            text = text,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = CalColors.text,
            fontSize = fontSize
        )
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
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()

    if (showConverter) {
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
}


@Composable
fun GregorianToJalaliConverter() {
    var gregorianYear by remember { mutableStateOf("") }
    var gregorianMonth by remember { mutableStateOf("") }
    var gregorianDay by remember { mutableStateOf("") }
    var jalaliDate by remember { mutableStateOf<CalendarConverter.Companion.JalaliDate?>(null) }
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()

    if (showConverter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Convert Gregorian to Jalali",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = gregorianYear,
                    onValueChange = { gregorianYear = it },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = gregorianMonth,
                    onValueChange = { gregorianMonth = it },
                    label = { Text("Month") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = gregorianDay,
                    onValueChange = { gregorianDay = it },
                    label = { Text("Day") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val year = gregorianYear.toInt()
                        val month = gregorianMonth.toInt()
                        val day = gregorianDay.toInt()
                        val gregorianDate = LocalDate.of(year, month, day)
                        jalaliDate = CalendarConverter.gregorianToJalali(gregorianDate)
                    } catch (e: Exception) {
                        jalaliDate = null
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Convert")
            }

            jalaliDate?.let {
                Text(
                    "Jalali Date: ${it.year}/${it.monthValue}/${it.dayOfMonth}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            } ?: run {
                Text(
                    "Enter a valid Gregorian date",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }
    }
}