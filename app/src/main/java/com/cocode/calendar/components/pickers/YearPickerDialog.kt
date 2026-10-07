package com.cocode.calendar.components.pickers

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.R
import java.time.LocalDate

/**
 * Dialog for selecting a year from a scrollable list.
 * Shows Jalali or Gregorian years, as the main screen does, and returns the year chosen
 * in that calendar.
 */
@Composable
fun YearPickerDialog(
    onDismiss: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val currentDate = viewModel.gregorianDate.observeAsState(LocalDate.now()).value
    val isJalaliCalendar = viewModel.isJalaliCalendar.observeAsState(false).value

    // Year range (can be adjusted as needed)
    val startGregorianYear = 1900
    val endGregorianYear = 2100
    val gregorianYears = (startGregorianYear..endGregorianYear).toList()

    // The list shows Jalali or Gregorian years, as the main screen does, and the highlighted
    // year is the one the main screen is in, in the same calendar.
    val displayYears = PickerUtils.convertYearsForDisplay(gregorianYears, isJalaliCalendar)
    val currentDisplayYear = PickerUtils.getCurrentYear(currentDate, isJalaliCalendar)

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
                text = stringResource(
                    if (isJalaliCalendar) R.string.picker_year_title_jalali
                    else R.string.picker_year_title_gregorian
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CalColors.background,
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
                    val isSelected = displayYear == currentDisplayYear

                    Button(
                        onClick = {
                            // The year is in the calendar the list shows (Jalali or Gregorian)
                            onYearSelected(displayYear)
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
                            .heightIn(min = 52.dp)
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
