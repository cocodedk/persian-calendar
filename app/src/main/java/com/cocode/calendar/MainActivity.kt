package com.cocode.calendar

import CalendarConverter
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import java.time.ZoneId
import java.time.ZonedDateTime

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
                    color = MaterialTheme.colorScheme.background
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
     * Changes the current month.
     * It sets the current Gregorian date to the first day of the new month.
     *
     * @param newYearMonth The new YearMonth to set.
     */
    fun changeMonth(newYearMonth: YearMonth) {
        val newDate = LocalDate.of(newYearMonth.year, newYearMonth.monthValue, 1)
        _gregorianDate.value = newDate
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

    // This Composable function is the main screen of the app.
    Column {
        // A button to toggle between Gregorian and Persian (Jalali) calendars.
        ToggleCalendarButton()

        // The header of the calendar view, which includes the current month and year,
        // and buttons to navigate to the next and previous months.
        CalendarHeader()

        // A Composable function that displays the current time in Iran.
        DisplayTimeInIran()

        // The header of the calendar view that displays the days of the week.
        WeekDaysHeader()

        // The grid of the calendar view that displays the dates.
        CalendarGrid(onDayClicked = {})

        // A button to update the date to today's date.
        TodayButton()

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
fun ToggleCalendarButton() {

    val viewModel: CalendarViewModel = viewModel()
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    // This composable function is a button to toggle between Gregorian and Persian (Jalali) calendars.
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Button(onClick = {viewModel.toggleIsJalaliCalendar()}) {
            Text(text = if (isJalaliCalendar) "> Gregorian" else "> Persian")
        }
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
    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    // Observe the gregorianDate LiveData from the ViewModel
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())
    // Observe the isJalaliCalendar LiveData from the ViewModel
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    // Get the YearMonth from the observed gregorianDate
    val yearMonth = YearMonth.from(gregorianDate)
    // Determine the display text for the header based on the current calendar mode
    val displayText = if(isJalaliCalendar) {
        // If the current calendar mode is Jalali, convert the gregorianDate to a Jalali string
        gregorianToJalaliString(gregorianDate)
    } else{
        // If the current calendar mode is Gregorian, format the gregorianDate to a string of "MMMM yyyy"
        gregorianDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    // Create a Row Composable for the header
    Row(
        // Arrange the children of the Row horizontally with space between them
        horizontalArrangement = Arrangement.SpaceBetween,
        // Apply a Modifier to the Row to fill the maximum width and add padding
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Create an IconButton Composable for navigating to the previous month
        IconButton(onClick = { viewModel.changeMonth(yearMonth.minusMonths(1)) }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
        }

        // Create a Text Composable for displaying the current month and year
        Text(
            text = displayText,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        // Create an IconButton Composable for navigating to the next month
        IconButton(onClick = { viewModel.changeMonth(yearMonth.plusMonths(1)) }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
        }
    }
    // Add a Spacer Composable to create space below the header
    Spacer(modifier = Modifier.height(8.dp))
}


/**
 * This Composable function represents the header of the calendar view that displays the days of the week.
 * It observes the current calendar mode (Gregorian or Jalali) from the ViewModel and displays the days of the week accordingly.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun WeekDaysHeader() {
    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    // Observe the isJalaliCalendar LiveData from the ViewModel
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    // Determine the days of the week to display based on the current calendar mode
    val daysOfWeek = if (isJalaliCalendar) {
        // If the current calendar mode is Jalali, use the short names for days in Persian
        listOf("ی", "د", "س", "چ", "پ", "ج", "ش")
    } else {
        // If the current calendar mode is Gregorian, use the English names for days
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }

    // Create a Row Composable for the header
    Row(
        // Arrange the children of the Row horizontally with space between them
        horizontalArrangement = Arrangement.SpaceBetween,
        // Align the children of the Row vertically in the center
        verticalAlignment = Alignment.CenterVertically,
        // Apply a Modifier to the Row to fill the maximum width and set a specific width
        modifier = Modifier
            .fillMaxWidth()
            .width(50.dp)
    ) {
        // Loop through each day in the daysOfWeek list
        for (day in daysOfWeek) {
            // Create a Box Composable for each day
            Box(
                // Align the content of the Box in the center
                contentAlignment = Alignment.Center,
                // Apply a Modifier to the Box to make each Box take up equal space, add padding, and add a border
                modifier = Modifier
                    .weight(1f)
                    .padding(6.dp)
                    .border(width = 1.dp, color = Color.Gray)
            ) {
                // Create a Text Composable to display the name of the day
                Text(
                    text = day,
                    modifier = Modifier
                )
            }
        }
    }
}


/**
 * This Composable function represents the grid of the calendar view that displays the dates.
 * It observes the current Gregorian date from the ViewModel and generates a grid of dates for the current month.
 * The grid includes dates from the previous month and the next month to fill the entire grid.
 * Each date in the grid is a Composable function that represents a day in the calendar.
 *
 * @param onDayClicked A lambda function that is invoked when a day in the calendar is clicked. It takes a LocalDate representing the clicked date.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarGrid(
    onDayClicked: (LocalDate) -> Unit
) {
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
                yearMonth = yearMonth,
                onDayClicked = onDayClicked
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
 * @param onDayClicked A lambda function that is invoked when a day in the calendar is clicked. It takes a LocalDate representing the clicked date.
 * @param updateDay A lambda function that is invoked to update the current day to the start day of the next week.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun WeekRow(
    startDate: LocalDate,
    daysInWeek: Int,
    yearMonth: YearMonth,
    onDayClicked: (LocalDate) -> Unit,
    updateDay: (LocalDate) -> Unit
) {

    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    // Observe the isJalaliCalendar LiveData from the ViewModel
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    // Create a Row Composable for the week
    Row(
        // Arrange the children of the Row horizontally with space between them
        horizontalArrangement = Arrangement.SpaceBetween,
        // Align the children of the Row vertically in the center
        verticalAlignment = Alignment.CenterVertically,
        // Apply a Modifier to the Row to fill the maximum width and add padding
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        // Initialize the current date to the start date
        var currentDate = startDate
        // Loop through each day in the week
        for (day in 1..daysInWeek) {
            // Initialize a JalaliDate with default values
            var jalaliDate = CalendarConverter.Companion.JalaliDate(0, 0, 0)
            // If the current calendar mode is Jalali, convert the current date to a Jalali date
            if (isJalaliCalendar) {
                jalaliDate = CalendarConverter.gregorianToJalali(currentDate)
            }
            // Create a DayBox Composable for each day
            DayBox(
                currentDate = currentDate,
                onDayClicked = onDayClicked,
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
 * @param onDayClicked A lambda function that is invoked when the box is clicked. It takes a LocalDate representing the clicked date.
 * @param isInCurrentMonth A boolean indicating whether the date is in the current month.
 * @param jalaliDate The Jalali date that the box represents, if the current calendar mode is Jalali.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun DayBox(
    currentDate: LocalDate,
    onDayClicked: (LocalDate) -> Unit,
    isInCurrentMonth: Boolean,
    jalaliDate: CalendarConverter.Companion.JalaliDate
) {

    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    // Observe the isJalaliCalendar LiveData from the ViewModel
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)
    // Observe the gregorianDate LiveData from the ViewModel
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    // Determine the background color and font color of the box based on certain conditions
    val backgroundColor = when {
        !isJalaliCalendar && !isInCurrentMonth -> Color.DarkGray
        !isJalaliCalendar && currentDate.isEqual(LocalDate.now()) -> Color(0xFF029a62)
        isJalaliCalendar && jalaliDate.monthValue != CalendarConverter.gregorianToJalali(gregorianDate).monthValue -> Color.DarkGray
        isJalaliCalendar && currentDate.isEqual(adjustDateForDeviceTimeZone()) -> Color(0xFF029a62) // Dark Green
        else -> Color.White
    }

    val fontColor = when {
        !isJalaliCalendar && !isInCurrentMonth -> Color.Gray
        !isJalaliCalendar && currentDate.isEqual(LocalDate.now()) -> Color.White
        isJalaliCalendar && jalaliDate.monthValue != CalendarConverter.gregorianToJalali(gregorianDate).monthValue -> Color.Gray
        isJalaliCalendar && currentDate.isEqual(adjustDateForDeviceTimeZone()) -> Color.White
        else -> Color.Black
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
            .width(50.dp)
            .height(50.dp)
            .padding(4.dp)
            .background(backgroundColor)
            .border(width = 1.dp, color = Color(0xFF029a62))
            .clickable(enabled = isInCurrentMonth) { onDayClicked(currentDate) }
    ) {
        // Create a Text Composable to display the date number
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = fontColor,
        )
    }
}


/**
 * Converts the provided Gregorian date to a Jalali date and returns it as a string.
 * The returned string is in the format of "MonthName Year".
 *
 * @param gregorianDate The Gregorian date to convert to a Jalali date.
 * @return A string representing the Jalali date.
 */
fun gregorianToJalaliString(gregorianDate: LocalDate): String {
    // Convert the provided Gregorian date to a Jalali date
    val jalaliMonth = CalendarConverter.toJalaliMonth(gregorianDate)
    // Return the Jalali date as a string in the format of "MonthName Year"
    return "${jalaliMonth.monthName} ${jalaliMonth.year}"
}


/**
 * Retrieves the current date and time in Iran.
 *
 * This function first defines the time zone for Iran (Asia/Tehran), which is usually UTC+3:30.
 * Then, it gets the current date and time in Iran by using the defined time zone.
 *
 * @return A ZonedDateTime object representing the current date and time in Iran.
 */
fun getCurrentTimeInIran(): ZonedDateTime {
    // Define the time zone for Iran. Iran Standard Time (IRST) is usually UTC+3:30
    val iranZoneId = ZoneId.of("Asia/Tehran")
    // Get the current date and time in Iran
    return ZonedDateTime.now(iranZoneId)
}


/**
 * Adjusts the current date to match the date in Iran's timezone.
 *
 * This function first retrieves the device's current timezone and the current date and time in Iran.
 * It then converts Iran's current time to the device's timezone.
 * If the local date is still behind Iran's date, it returns Iran's current date.
 * Otherwise, it returns the local date.
 *
 * @return A LocalDate object representing the adjusted date.
 */
fun adjustDateForDeviceTimeZone(): LocalDate {
    // Get the device's current time zone
    val deviceZoneId = ZoneId.systemDefault()
    // Get the current time in Iran
    val currentTimeInIran = getCurrentTimeInIran()
    // Convert Iran's current time to the local time zone
    val deviceTime = currentTimeInIran.withZoneSameInstant(deviceZoneId)

    // Check if the local time has not yet reached the current day in Iran
    return if (deviceTime.toLocalDate().isBefore(currentTimeInIran.toLocalDate())) {
        // If the local date is still behind Iran's date, use Iran's current date
        currentTimeInIran.toLocalDate()
    } else {
        // Otherwise, use the local date and time
        deviceTime.toLocalDate()
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
            val iranTime = getCurrentTimeInIran()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss Z")
            val formattedIranTime = iranTime.format(formatter)
            currentTime.value = formattedIranTime
            delay(1000)
        }
    }
    // put the text in a row and center it
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Iran time is: ${currentTime.value}")
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
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        // Create a Button Composable
        Button(
            // Set the click event handler for the button
            // When the button is clicked, it calls the updateGregorianDate function of the CalendarViewModel with today's date
            onClick = { viewModel.updateGregorianDate(LocalDate.now()) }
        ) {
            // Set the display text for the button
            Text(text = "> Today")
        }
    }

}
