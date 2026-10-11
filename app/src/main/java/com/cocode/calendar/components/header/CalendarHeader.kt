package com.cocode.calendar.components.header

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import com.cocode.calendar.CalColors
import com.cocode.calendar.R

/**
 * Displays a header row containing the days of the week.
 *
 * This composable function creates a horizontal row that shows the abbreviated names
 * of the days of the week (Sun, Mon, Tue, etc.). The row is styled with a border
 * and rounded corners at the top.
 *
 * The day names come from the `weekday_short` string array, Sunday first.
 *
 * @see DayOfWeekBox
 */
@Composable
fun WeekDaysHeader() {
    val daysOfWeek = stringArrayResource(R.array.weekday_short)

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
        // The grid starts on Sunday, so Sunday (first) and Saturday (last) are the weekend columns.
        daysOfWeek.forEachIndexed { index, day ->
            DayOfWeekBox(day, isWeekend = index == 0 || index == daysOfWeek.lastIndex)
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
 * @param isWeekend Whether the day is shown in the weekend colour. When left out, the first
 * and last day of `weekday_short` (Sunday and Saturday) count as the weekend, as before.
 */
@Composable
fun DayOfWeekBox(day: String, isWeekend: Boolean? = null) {
    val weekDays = stringArrayResource(R.array.weekday_short)
    val weekend = isWeekend ?: (day == weekDays.first() || day == weekDays.last())
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(55.dp)
            .height(40.dp)
    ) {
        val color = if (weekend) CalColors.weekend_text else CalColors.weekday_text
        Text(
            text = day,
            color = color
        )
    }
}
