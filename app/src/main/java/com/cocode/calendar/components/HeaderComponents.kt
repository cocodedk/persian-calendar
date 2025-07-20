package com.cocode.calendar.components

import CalendarConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
 * This Composable function represents the header section of the calendar application.
 * It displays the current month and year, alternative calendar info, and Iran time
 * with a beautiful gradient background and styled text.
 *
 * @Composable This annotation indicates that this function is a Composable function
 * in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun HeaderSection() {
    val viewModel: CalendarViewModel = viewModel()
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    /* ---------- build title strings (unchanged) ---------- */
    val (primaryText, secondaryText) = remember(gregorianDate, isJalaliCalendar) {
        val jalaliMonths = CalendarConverter.gregorianToJalaliMonths(gregorianDate)
        val jalaliDate = CalendarConverter.gregorianToJalali(gregorianDate)
        val jalaliWeek = CalendarConverter.getJalaliWeekNumber(jalaliDate)

        val jalaliText = buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 18.sp)) { append("week $jalaliWeek") }
            append(" - ${jalaliMonths["left"]?.monthName} - ${jalaliMonths["right"]?.monthName} ${jalaliMonths["right"]?.year}")
        }

        val gregorianText = buildAnnotatedString {
            append("${gregorianDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))} - ")
            withStyle(SpanStyle(fontSize = 18.sp)) {
                append("week ${DateTimeUtils.getCurrentWeekNumber(gregorianDate)}")
            }
        }

        if (isJalaliCalendar) jalaliText to gregorianText else gregorianText to jalaliText
    }

    /* ---------- ticking clock (unchanged) ---------- */
    val currentTime = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (currentCoroutineContext().isActive) {
            currentTime.value = DateTimeUtils
                .getCurrentTimeInIran()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            delay(1_000)
        }
    }

    /* ---------- Header with top and bottom dividers ---------- */
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Divider(
            color = CalColors.weekend_text,
            thickness = 1.dp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = primaryText,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = CalColors.active_text,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = secondaryText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = CalColors.inactive_text,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Iran time: ${currentTime.value}",
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            color = CalColors.text,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Divider(
            color = CalColors.weekend_text,
            thickness = 1.dp
        )
    }
}

/**
 * Legacy CalendarHeader composable - kept for backward compatibility
 * @deprecated Use HeaderSection instead
 */
@Deprecated("Use HeaderSection instead")
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
 * Legacy DisplayTimeInIran composable - kept for backward compatibility
 * @deprecated Use HeaderSection instead
 */
@Deprecated("Use HeaderSection instead")
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
