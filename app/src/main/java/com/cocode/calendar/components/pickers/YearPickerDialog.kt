package com.cocode.calendar.components.pickers

import CalendarConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import java.time.LocalDate

/**
 * Dialog for selecting a year from a scrollable list.
 * Supports both Gregorian and Jalali calendar systems with automatic conversion.
 */
@Composable
fun YearPickerDialog(
    onDismiss: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val currentDate = viewModel.gregorianDate.observeAsState(LocalDate.now()).value
    val isJalaliCalendar = viewModel.isJalaliCalendar.observeAsState(false).value

    val currentGregorianYear = currentDate?.year ?: LocalDate.now().year

    // Year range (can be adjusted as needed)
    val startGregorianYear = 1900
    val endGregorianYear = 2100
    val gregorianYears = (startGregorianYear..endGregorianYear).toList()

    // Convert to display years and current year based on calendar mode
    val (displayYears, currentDisplayYear) = if (isJalaliCalendar) {
        // Convert Gregorian years to Jalali years for display
        val jalaliYears = gregorianYears.map { gregorianYear ->
            val tempDate = LocalDate.of(gregorianYear, 6, 15) // Use middle of year for conversion
            CalendarConverter.gregorianToJalali(tempDate).year
        }

        // Get current Jalali year
        val currentJalaliYear = CalendarConverter.gregorianToJalali(currentDate ?: LocalDate.now()).year

        jalaliYears to currentJalaliYear
    } else {
        gregorianYears to currentGregorianYear
    }

    // Calculate the index of the current year in the display list
    val currentYearIndex = displayYears.indexOf(currentDisplayYear)

    // Remember the LazyColumn state for scrolling control
    val listState = rememberLazyListState()

    // Auto-scroll to current year when dialog opens
    LaunchedEffect(Unit) {
        if (currentYearIndex >= 0) {
            // Scroll to current year, centering it in the visible area
            listState.scrollToItem(
                index = maxOf(0, currentYearIndex - 2) // Show current year with some context above
            )
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
                .height(400.dp)
        ) {
            Text(
                text = "Select Year",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CalColors.active_text,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Scrollable list of years
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(displayYears.indices.toList()) { index ->
                    val displayYear = displayYears[index]
                    val gregorianYear = gregorianYears[index]
                    val isSelected = displayYear == currentDisplayYear

                    Button(
                        onClick = {
                            // Always pass the Gregorian year to maintain internal consistency
                            onYearSelected(gregorianYear)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                CalColors.active_button_background
                            else CalColors.button_background
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .height(52.dp)
                    ) {
                        Text(
                            text = displayYear.toString(),
                            color = CalColors.text,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp
                        )
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
