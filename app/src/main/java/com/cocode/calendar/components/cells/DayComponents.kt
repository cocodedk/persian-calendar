package com.cocode.calendar.components.cells

import CalendarConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.Event
import com.cocode.calendar.models.JalaliDate
import utils.DateTimeUtils
import java.time.LocalDate

/**
 * This Composable function represents a box that displays a date in the calendar grid.
 * It observes the current calendar mode (Gregorian or Jalali) from the ViewModel and generates a box for the date.
 * The box includes the date number and is colored based on certain conditions.
 *
 * @param currentDate The current date that the box represents.
 * @param isInCurrentMonth A boolean indicating whether the date is in the current month.
 * @param jalaliDate The Jalali date that the box represents, if the current calendar mode is Jalali.
 */
@Composable
fun DayBox(
    currentDate: LocalDate,
    isInCurrentMonth: Boolean,
    events: List<Event>
) {
    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    // Observe the isJalaliCalendar LiveData from the ViewModel
    val isJalaliCalendar = viewModel.isJalaliCalendar.observeAsState(initial = false).value
    // Observe the gregorianDate LiveData from the ViewModel
    val gregorianDate = viewModel.gregorianDate.observeAsState(initial = LocalDate.now()).value

    // Convert current date to Jalali for comparison
    val jalaliDate = CalendarConverter.gregorianToJalali(currentDate)

    // Convert current gregorian date to Jalali for comparison
    val convertedGregorianDate = CalendarConverter.gregorianToJalali(gregorianDate)

    // Determine the background color and font color of the box based on certain conditions

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
