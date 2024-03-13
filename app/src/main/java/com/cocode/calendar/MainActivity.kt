package com.cocode.calendar

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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.ZoneId
import java.time.ZonedDateTime

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalendarApp()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarApp() {
    // This state is remembered across recompositions but not configuration changes like rotations.
    var isJalaliCalendar by remember { mutableStateOf(false) }

    /*val adjustedDate = adjustDateForDeviceTimeZone()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")
    val formattedDate = adjustedDate.format(formatter)

    val iranTime = getCurrentTimeInIran()
    val formattedIranTime = iranTime.format(formatter)*/


    // This is the main screen composable of your app
    CalendarScreen(isJalaliCalendar) {
        // This lambda is triggered when the button is pressed
        isJalaliCalendar = !isJalaliCalendar
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(isJalaliCalendar: Boolean, onToggleCalendar: () -> Unit) {
    Column {
        ToggleCalendarButton(isJalaliCalendar, onToggleCalendar) // Button to toggle calendar view
        CalendarView(isJalaliCalendar = isJalaliCalendar) // Calendar view to display dates
    }
}

@Composable
fun ToggleCalendarButton(isJalaliCalendar: Boolean, onToggleCalendar: () -> Unit) {
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
fun CalendarView(
    isJalaliCalendar: Boolean = false,
    yearMonth: YearMonth = YearMonth.now(),
    onDayClicked: (LocalDate) -> Unit = {}
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        CalendarHeader(yearMonth = yearMonth, isJalaliCalendar)
        DisplayTimeInIran()
        WeekDaysHeader(isJalaliCalendar)
        CalendarGrid(yearMonth = yearMonth, onDayClicked = onDayClicked, isJalaliCalendar)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarHeader(yearMonth: YearMonth, isJalaliCalendar: Boolean) {
    val displayText = if(isJalaliCalendar) {
        gregorianToJalaliString(yearMonth)
    } else{
        yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    Text(
        text = displayText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun WeekDaysHeader(isJalaliCalendar: Boolean) {
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
    yearMonth: YearMonth,
    onDayClicked: (LocalDate) -> Unit,
    isJalaliCalendar: Boolean
) {
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
                onDayClicked = onDayClicked,
                isJalaliCalendar = isJalaliCalendar
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
    isJalaliCalendar: Boolean,
    updateDay: (LocalDate) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        var currentDate = startDate
        for (day in 1..daysInWeek) {
            var jaliliDate = LocalDate.now()
            if (isJalaliCalendar) {
                jaliliDate = PersianCalendarConverter.gregorianToJalali(
                    currentDate.year,
                    currentDate.monthValue,
                    currentDate.dayOfMonth
                )
            }
            DayBox(
                currentDate = currentDate,
                onDayClicked = onDayClicked,
                isJalaliCalendar = isJalaliCalendar,
                jaliliDate = jaliliDate,
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
    isJalaliCalendar: Boolean,
    jaliliDate: LocalDate
) {
    // using when to determine modifier's background color
    val backgroundColor = when {
        !isInCurrentMonth -> Color.LightGray
        !isJalaliCalendar && currentDate.isEqual(LocalDate.now()) -> Color.Green
        isJalaliCalendar && currentDate.isEqual(adjustDateForDeviceTimeZone()) -> Color.Green
        else -> Color.White
    }

    val text = if(isJalaliCalendar) {
        jaliliDate.dayOfMonth.toString()
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
fun gregorianToJalaliString(yearMonth: YearMonth): String {
    // Convert the provided YearMonth to the first day of that month

    // Format the Jalali date as a string (assuming you have the month names in an array or a way to get them)
    val (monthName, year, _) = PersianCalendarConverter.getJalaliMonthName(yearMonth) // Implement this according to your localization
    return "$monthName $year"
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTimeInIran(): ZonedDateTime {
    // Define the time zone for Iran. Iran Standard Time (IRST) is usually UTC+3:30
    val iranZoneId = ZoneId.of("Asia/Tehran")
    // Get the current date and time in Iran
    return ZonedDateTime.now(iranZoneId)
}

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayTimeInIran() {
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
