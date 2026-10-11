package com.cocode.calendar.components.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.R
import java.time.LocalDate

/**
 * Dialog for selecting a month from a 3x4 grid layout.
 * Shows Jalali or Gregorian month names, as the main screen does, and returns the month number
 * (1-12) in that calendar.
 */
@Composable
fun MonthPickerDialog(
    onDismiss: () -> Unit,
    onMonthSelected: (Int) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val currentDate = viewModel.gregorianDate.observeAsState(LocalDate.now()).value
    val isJalaliCalendar = viewModel.isJalaliCalendar.observeAsState(false).value

    // The names come from the string resources: Jalali or Gregorian, as the main screen shows.
    // The highlighted month is the one the main screen is in, in the same calendar.
    val months = PickerUtils.getMonthNames(LocalResources.current, isJalaliCalendar)
    val currentMonth = PickerUtils.getCurrentMonth(currentDate, isJalaliCalendar)
    val columns = if (LocalDensity.current.fontScale > 1.3f) 2 else 3

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(
                    if (isJalaliCalendar) R.string.picker_month_title_jalali
                    else R.string.picker_month_title_gregorian
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CalColors.background,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 3x4 grid of months (2x6 at large text sizes, so the names still fit)
            repeat(12 / columns) { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(columns) { col ->
                        val monthIndex = row * columns + col
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
                                    .heightIn(min = 48.dp)
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
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
