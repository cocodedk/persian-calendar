package com.cocode.calendar.components

import CalendarConverter
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import utils.DateTimeUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
