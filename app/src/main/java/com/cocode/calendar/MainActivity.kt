package com.cocode.calendar

import CalendarConverter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
 *
 * @RequiresApi(Build.VERSION_CODES.O) This annotation indicates that this class requires API level 26 (Android 8.0, Oreo) or higher.
 */
class MainActivity : ComponentActivity() {

    /**
     * This function is called when the activity is starting.
     * This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     * @RequiresApi(Build.VERSION_CODES.O) This annotation indicates that this function requires API level 26 (Android 8.0, Oreo) or higher.
     */
    @RequiresApi(Build.VERSION_CODES.O)
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


class CalendarViewModel : ViewModel() {
    // MutableStateFlow to hold gregorianDate
    @RequiresApi(Build.VERSION_CODES.O)
    private val _gregorianDate = MutableStateFlow(LocalDate.now())
    // MutableStateFlow to hold isJalaliCalendar
    private val _isJalaliCalendar = MutableStateFlow(false)

    // Expose an immutable LiveData for observers
    @RequiresApi(Build.VERSION_CODES.O)
    val gregorianDate = _gregorianDate.asLiveData()
    val isJalaliCalendar = _isJalaliCalendar.asLiveData()

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateGregorianDate(newDate: LocalDate) {
        _gregorianDate.value = newDate
    }

    fun toggleIsJalaliCalendar() {
        _isJalaliCalendar.value = !_isJalaliCalendar.value
    }
}

/**
 * This Composable function represents the main application for the calendar.
 *
 * @RequiresApi(Build.VERSION_CODES.O) This annotation indicates that this function requires API level 26 (Android 8.0, Oreo) or higher.
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarApp() {
    val viewModel: CalendarViewModel = viewModel()
    viewModel.isJalaliCalendar.observeAsState(initial = false)
    viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    CalendarScreen(
        onToggleCalendar = {
            viewModel.toggleIsJalaliCalendar()
        },
        onMonthChange = { newYearMonth ->
            val newDate = LocalDate.of(newYearMonth.year, newYearMonth.monthValue, 1)
            viewModel.updateGregorianDate(newDate)
        }
    )
}


/**
 * This Composable function represents the main screen of the calendar application.
 *
 * @param onToggleCalendar A lambda function that is triggered when the user toggles between Gregorian and Jalali calendars.
 * @param onMonthChange A lambda function that is triggered when the user navigates to a different month.
 *
 * @RequiresApi(Build.VERSION_CODES.O) This annotation indicates that this function requires API level 26 (Android 8.0, Oreo) or higher.
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    onToggleCalendar: () -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {

    // This Composable function is the main screen of the app.
    Column {
        // A button to toggle between Gregorian and Persian (Jalali) calendars.
        ToggleCalendarButton(onToggleCalendar)

        // The header of the calendar view, which includes the current month and year, and buttons to navigate to the next and previous months.
        // ...
        CalendarHeader(onMonthChange = onMonthChange)

        // A Composable function that displays the current time in Iran.
        DisplayTimeInIran()

        // The header of the calendar view that displays the days of the week.
        WeekDaysHeader()

        // The grid of the calendar view that displays the dates.
        CalendarGrid(onDayClicked = {})
    }
}


/**
 * This Composable function represents a button that toggles the calendar view between Gregorian and Persian (Jalali) modes.
 *
 * @param onToggleCalendar A lambda function that is triggered when the button is clicked. This function should handle the logic for toggling the calendar view.
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun ToggleCalendarButton(onToggleCalendar: () -> Unit) {

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
        Button(onClick = onToggleCalendar) {
            Text(text = if (isJalaliCalendar) "> Gregorian" else "> Persian")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarHeader(onMonthChange: (YearMonth) -> Unit) {
    val viewModel: CalendarViewModel = viewModel()
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    val yearMonth = YearMonth.from(gregorianDate)
    // This composable function is the header of the calendar view.
    val displayText = if(isJalaliCalendar) {
        gregorianToJalaliString(gregorianDate)
    } else{
        gregorianDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        IconButton(onClick = { onMonthChange(yearMonth.minusMonths(1)) }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
        }

        Text(
            text = displayText,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        IconButton(onClick = { onMonthChange(yearMonth.plusMonths(1)) }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}


@Composable
fun WeekDaysHeader() {
    val viewModel: CalendarViewModel = viewModel()
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    // This composable function is the header of the calendar view that displays the days of the week.
    val daysOfWeek = if (isJalaliCalendar) {
        listOf("ی", "د", "س", "چ", "پ", "ج", "ش") // Short names for days in Persian
    } else {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        for (day in daysOfWeek) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier
                .weight(1f) // This makes each Box take up equal space
                .padding(4.dp)
                .border(width = 1.dp, color = Color.Gray)) {
                Text(
                    text = day,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(
    onDayClicked: (LocalDate) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    val yearMonth = YearMonth.from(gregorianDate)
    // This composable function is the grid of the calendar view that displays the dates.
    val daysInWeek = WeekFields.of(Locale.getDefault()).dayOfWeek().range().maximum.toInt()
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    // Determine the start day for the calendar grid to include days from the previous month
    val startDayOfWeek = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

    // Determine the end day for the calendar grid to include days from the next month
    val endDayOfWeek = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

    var currentDay = startDayOfWeek

    Column {
        while (currentDay <= endDayOfWeek) {
            WeekRow(
                startDate = currentDay,
                daysInWeek = daysInWeek,
                yearMonth = yearMonth,
                onDayClicked = onDayClicked
            ) {
                currentDay = currentDay.plusDays(7)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekRow(
    startDate: LocalDate,
    daysInWeek: Int,
    yearMonth: YearMonth,
    onDayClicked: (LocalDate) -> Unit,
    updateDay: (LocalDate) -> Unit
) {

    val viewModel: CalendarViewModel = viewModel()
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    // This composable function is a row in the calendar grid that displays the dates for a week.
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        var currentDate = startDate
        for (day in 1..daysInWeek) {
            var jalaliDate = CalendarConverter.Companion.JalaliDate(0, 0, 0)
            if (isJalaliCalendar) {
                jalaliDate = CalendarConverter.gregorianToJalali(currentDate)
            }
            DayBox(
                currentDate = currentDate,
                onDayClicked = onDayClicked,
                jalaliDate = jalaliDate,
                isInCurrentMonth = currentDate.month == yearMonth.month
            )
            currentDate = currentDate.plusDays(1)
        }
        updateDay(currentDate)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayBox(
    currentDate: LocalDate,
    onDayClicked: (LocalDate) -> Unit,
    isInCurrentMonth: Boolean,
    jalaliDate: CalendarConverter.Companion.JalaliDate
) {

    val viewModel: CalendarViewModel = viewModel()
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    // This composable function is a box that displays a date in the calendar grid.
    // using when to determine modifier's background color
    val backgroundColor = when {
        !isJalaliCalendar && !isInCurrentMonth -> Color.DarkGray
        !isJalaliCalendar && currentDate.isEqual(LocalDate.now()) -> Color.Green
        isJalaliCalendar && jalaliDate.monthValue != CalendarConverter.gregorianToJalali(gregorianDate).monthValue -> Color.LightGray
        isJalaliCalendar && currentDate.isEqual(adjustDateForDeviceTimeZone()) -> Color.Green
        else -> Color.White
    }

    val text = if(isJalaliCalendar) {
        jalaliDate.dayOfMonth.toString()
    } else {
        currentDate.dayOfMonth.toString()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .padding(4.dp)
            .background(backgroundColor)
            .border(width = 1.dp, color = Color.Gray)
            .clickable(enabled = isInCurrentMonth) { onDayClicked(currentDate) }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (!isInCurrentMonth) Color.Gray else Color.Black,
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun gregorianToJalaliString(gregorianDate: LocalDate): String {

    // This function converts the provided YearMonth to a Jalali date and returns it as a string.
    // Convert the provided YearMonth to the first day of that month

    // Format the Jalali date as a string (assuming you have the month names in an array or a way to get them)
    val jalaliMonth = CalendarConverter.toJalaliMonth(gregorianDate)
    return "${jalaliMonth.monthName} ${jalaliMonth.year}"
}


@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTimeInIran(): ZonedDateTime {
    // This function gets the current date and time in Iran.
    // Define the time zone for Iran. Iran Standard Time (IRST) is usually UTC+3:30
    val iranZoneId = ZoneId.of("Asia/Tehran")
    // Get the current date and time in Iran
    return ZonedDateTime.now(iranZoneId)
}


@RequiresApi(Build.VERSION_CODES.O)
fun adjustDateForDeviceTimeZone(): LocalDate {
    // Returns the current date in Iran if the local time has not yet reached the current day in Iran.
    // Otherwise, it returns the local date and time.

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


@RequiresApi(Build.VERSION_CODES.O)
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
