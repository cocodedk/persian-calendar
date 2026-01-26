package com.cocode.calendar.components.pickers

import CalendarConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import java.time.LocalDate

/**
 * Dialog for selecting a month from a 3x4 grid layout.
 * Allows users to select a month from the current calendar system.
 */
@Composable
fun MonthPickerDialog(
    onDismiss: () -> Unit,
    onMonthSelected: (Int) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val currentDate = viewModel.gregorianDate.observeAsState(LocalDate.now()).value
    val isJalaliCalendar = viewModel.isJalaliCalendar.observeAsState(false).value

    // Use centralized month names from Strings object
    val months = com.cocode.calendar.components.pickers.PickerUtils.getMonthNames(isJalaliCalendar)
    val currentMonth = if (isJalaliCalendar) {
        // Convert current Gregorian date to Jalali and get the Jalali month
        currentDate?.let { CalendarConverter.gregorianToJalali(it).monthValue } ?: 1
    } else {
        currentDate?.monthValue ?: 1
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Select Month",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CalColors.active_text,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 3x4 grid of months
            repeat(4) { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(3) { col ->
                        val monthIndex = row * 3 + col
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
                                    .height(48.dp)
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
                    .height(48.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
