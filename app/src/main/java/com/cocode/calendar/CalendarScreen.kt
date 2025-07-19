package com.cocode.calendar

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.YearMonth

/**
 * This Composable function represents the main application for the calendar.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarApp() {
    val viewModel: CalendarViewModel = viewModel()
    viewModel.isJalaliCalendar.observeAsState(initial = false)
    viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    CalendarScreen()
}

/**
 * This Composable function represents the main screen of the calendar application.
 * @Composable This annotation indicates that this function is a Composable function
 * in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarScreen() {

    // Get an instance of the CalendarViewModel
    val viewModel: CalendarViewModel = viewModel()
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    // Use Box to allow absolute positioning
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content in a Column
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // The header of the calendar view, which includes the current month and year,
            // and buttons to navigate to the next and previous months.
            CalendarHeader()

            // A Composable function that displays the current time in Iran.
            DisplayTimeInIran()

            // The header of the calendar view that displays the days of the week.
            WeekDaysHeader()

            // The grid of the calendar view that displays the dates.
            CalendarGrid()

            // The controls for the calendar view, including a button to navigate to today's date
            CalControls()

            CalendarConverterBox()

            CrossClickArea(
                onClickRight = { viewModel.changeMonth(YearMonth.from(gregorianDate).plusMonths(1)) },
                onClickLeft = { viewModel.changeMonth(YearMonth.from(gregorianDate).minusMonths(1)) },
                onClickUp = { viewModel.changeYear(gregorianDate.year + 1) },
                onClickDown = { viewModel.changeYear(gregorianDate.year - 1) },
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )

            // Add spacer to push content above footer
            Spacer(modifier = Modifier.weight(1f))
        }

        // Footer positioned at absolute bottom
        FooterInfo(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

/**
 * This Composable function displays the footer information with the developer name and company.
 * It shows "Babak Bandpey" and "cocode.dk" at the bottom of the screen.
 *
 * @param modifier Modifier to be applied to the footer container
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose.
 */
@Composable
fun FooterInfo(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val annotatedString = buildAnnotatedString {
            pushStringAnnotation(tag = "URL", annotation = "https://www.linkedin.com/in/babakbandpey/")
            withStyle(
                style = SpanStyle(
                    color = CalColors.active_text,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Babak Bandpey")
            }
            pop()
            withStyle(
                style = SpanStyle(
                    color = CalColors.inactive_text
                )
            ) {
                append(" - ")
            }
            pushStringAnnotation(tag = "URL", annotation = "https://cocode.dk")
            withStyle(
                style = SpanStyle(
                    color = CalColors.active_text,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("cocode.dk")
            }
            pop()
        }

        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            ),
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                        context.startActivity(intent)
                    }
            }
        )
    }
}
