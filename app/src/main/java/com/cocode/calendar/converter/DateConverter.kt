package com.cocode.calendar.converter

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.R
import com.cocode.calendar.components.date.DateFormattingUtils

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
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun DateConverter(
    showJalaliToGregorianConverter: Boolean,
    showGregorianToJalaliConverter: Boolean
) {
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    val convertedDate = DateConversion.convert(showJalaliToGregorianConverter, year, month, day)
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()

    // The result (or the invalid-date message) is scrolled into view after every edit, a change of
    // direction and each time the keyboard opens, so neither the keyboard nor a large text size
    // can hide it. A message that stays the same (an invalid date edited to another invalid date)
    // needs this as much as a new result.
    val resultRequester = remember { BringIntoViewRequester() }
    val resultShown = convertedDate != null || DateFormattingUtils.hasCompleteInput(year, month, day)
    val keyboardVisible = WindowInsets.isImeVisible
    LaunchedEffect(resultShown, year, month, day, showJalaliToGregorianConverter, keyboardVisible) {
        if (resultShown) {
            withFrameNanos { }
            resultRequester.bringIntoView()
        }
    }

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
                        text = stringResource(
                            if (showJalaliToGregorianConverter) R.string.converter_enter_jalali
                            else if (showGregorianToJalaliConverter) R.string.converter_enter_gregorian
                            else R.string.converter_problem
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )

                    val focusManager = LocalFocusManager.current

                    DateInputFields(
                        year, month, day,
                        onYearChange = { newYear ->
                            year = newYear
                            if (newYear.length == FieldAdvance.YEAR_DIGITS) {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        },
                        onMonthChange = { newMonth ->
                            month = newMonth
                            // Move on only when no other digit could follow: a "1" may become 10-12
                            if (FieldAdvance.isFull(newMonth, FieldAdvance.MONTH_MAX)) {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        },
                        onDayChange = { day = it },
                        onYearDone = { focusManager.moveFocus(FocusDirection.Next) },
                        onMonthDone = { focusManager.moveFocus(FocusDirection.Next) }
                    )
                }
            }

            // Result section
            DisplayConvertedDate(
                convertedDate, year, month, day,
                modifier = Modifier.bringIntoViewRequester(resultRequester)
            )

            if (convertedDate != null) {
                DisplayPeriodToNow(convertedDate, year, month, day)
            }
        }
    }
}
