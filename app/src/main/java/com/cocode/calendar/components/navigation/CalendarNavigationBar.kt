package com.cocode.calendar.components.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth

/**
 * Month and Year navigation buttons that open selection dialogs.
 * Replaces the old CrossClickArea navigation system.
 */
@Composable
fun CalendarNavigation() {
    val viewModel: CalendarViewModel = viewModel()
    val showConverter = viewModel.showConverter.collectAsState().value

    // Don't show navigation when converter is visible
    if (showConverter) {
        return
    }

    // State for dialog visibility
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Month Selection Button
        Button(
            onClick = { showMonthPicker = true },
            colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .height(48.dp)
        ) {
            Text(
                text = "Select Month",
                color = CalColors.text,
                fontWeight = FontWeight.Bold
            )
        }

        // Year Selection Button
        Button(
            onClick = { showYearPicker = true },
            colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .height(48.dp)
        ) {
            Text(
                text = "Select Year",
                color = CalColors.text,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Current date for month selection
    val currentDate = viewModel.gregorianDate.observeAsState(LocalDate.now()).value

    // Show dialogs when requested
    if (showMonthPicker) {
        com.cocode.calendar.components.pickers.MonthPickerDialog(
            onDismiss = { showMonthPicker = false },
            onMonthSelected = { month ->
                val newYearMonth = YearMonth.of(currentDate?.year ?: LocalDate.now().year, month)
                viewModel.changeMonth(newYearMonth)
                showMonthPicker = false
            }
        )
    }

    if (showYearPicker) {
        com.cocode.calendar.components.pickers.YearPickerDialog(
            onDismiss = { showYearPicker = false },
            onYearSelected = { year ->
                viewModel.changeYear(year)
                showYearPicker = false
            }
        )
    }
}
