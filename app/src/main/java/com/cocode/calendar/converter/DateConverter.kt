package com.cocode.calendar.converter

import CalendarConverter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalendarViewModel
import java.time.LocalDate

/**
 * A composable function that creates a date converter interface.
 *
 * This function provides a user interface for converting dates between Jalali and Gregorian calendars.
 * It includes input fields for year, month, and day, a convert button, and displays the converted date.
 *
 * @param showJalaliToGregorianConverter A boolean flag indicating whether to show the Jalali to Gregorian converter.
 * @param showGregorianToJalaliConverter A boolean flag indicating whether to show the Gregorian to Jalali converter.
 *
 * @return This function doesn't return a value, but creates and displays a Composable UI for date conversion.
 */
@Composable
fun DateConverter(
    showJalaliToGregorianConverter: Boolean,
    showGregorianToJalaliConverter: Boolean
) {
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var convertedDate by remember { mutableStateOf<Any?>(null) }
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()

    if (showConverter) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Input section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (showJalaliToGregorianConverter) "Enter Jalali Date"
                        else if (showGregorianToJalaliConverter) "Enter Gregorian Date"
                        else "Something is wrong",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )

                    val focusManager = LocalFocusManager.current

                    fun convertDate() {
                        convertedDate = try {
                            val y = year.toInt()
                            val m = month.toInt()
                            val d = day.toInt()
                            if (showJalaliToGregorianConverter) {
                                CalendarConverter.jalaliToGregorian(y, m, d)
                            } else if (showGregorianToJalaliConverter) {
                                CalendarConverter.gregorianToJalali(LocalDate.of(y, m, d))
                            } else {
                                throw IllegalArgumentException("Invalid conversion type")
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }

                    DateInputFields(
                        year, month, day,
                        onYearChange = { newYear ->
                            year = newYear
                            if (newYear.length == 4) {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        },
                        onMonthChange = { newMonth ->
                            month = newMonth
                            val monthNum = newMonth.toIntOrNull()
                            if (monthNum != null && monthNum in 1..12) {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        },
                        onDayChange = { day = it },
                        onYearDone = { focusManager.moveFocus(FocusDirection.Next) },
                        onMonthDone = { focusManager.moveFocus(FocusDirection.Next) },
                        onDayDone = { convertDate() }
                    )
                }
            }

            // Result section
            DisplayConvertedDate(convertedDate, year, month, day)

            if (convertedDate != null) {
                DisplayPeriodToNow(convertedDate, year, month, day)
            }
        }
    }
}
