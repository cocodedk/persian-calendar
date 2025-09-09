package com.cocode.calendar

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.components.HeaderSection
import com.cocode.calendar.components.WeekDaysHeader
import com.cocode.calendar.components.CalendarGrid
import com.cocode.calendar.components.CalControls
import com.cocode.calendar.components.CalendarNavigation
import com.cocode.calendar.components.EventCreationDialog
import com.cocode.calendar.components.EventListDialog
import com.cocode.calendar.converter.CalendarConverterBox
import java.time.LocalDate
import java.time.YearMonth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember

/**
 * This Composable function represents the main application for the calendar.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarApp() {
    val context = LocalContext.current
    val eventDao = remember { AppDatabase.getDatabase(context).eventDao() }
    val viewModel: CalendarViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CalendarViewModel(eventDao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )
    viewModel.isJalaliCalendar.observeAsState(initial = false)
    viewModel.gregorianDate.observeAsState(initial = java.time.LocalDate.now())

    CalendarScreen()
}

/**
 * This Composable function represents the main screen of the calendar application.
 * @Composable This annotation indicates that this function is a Composable function
 * in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarScreen() {

    // Use Box to allow absolute positioning
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content in a Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Leave space for footer
        ) {
            // The header section with gradient background, calendar info, and Iran time
            HeaderSection()

            // The header of the calendar view that displays the days of the week.
            WeekDaysHeader()

            // The grid of the calendar view that displays the dates.
            CalendarGrid()

            // The controls for the calendar view, including a button to navigate to today's date
            CalControls()

            // Month and Year selection navigation
            CalendarNavigation()
        }

        // Date converter overlay - positioned on top of everything
        CalendarConverterBox()

        // Event creation dialog
        EventCreationDialog()

        // Event list dialog
        EventListDialog()

        // Footer positioned at the bottom
        FooterInfo(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * This Composable function displays the footer information with the developer name and company
 * in a fancy, professional style. It shows "Babak Bandpey" and "cocode.dk" with enhanced visual design.
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Fancy container with background and rounded corners
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CalColors.prev_month_background.copy(alpha = 0.8f))
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            val annotatedString = buildAnnotatedString {
                // Developer name with enhanced styling
                pushStringAnnotation(tag = "URL", annotation = "https://www.linkedin.com/in/babakbandpey/")
                withStyle(
                    style = SpanStyle(
                        color = CalColors.active_text,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("Babak Bandpey")
                }
                pop()

                // Elegant separator
                withStyle(
                    style = SpanStyle(
                        color = CalColors.inactive_text,
                        fontWeight = FontWeight.Light
                    )
                ) {
                    append(" • ")
                }

                // Company name with enhanced styling
                pushStringAnnotation(tag = "URL", annotation = "https://cocode.dk")
                withStyle(
                    style = SpanStyle(
                        color = CalColors.active_text,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium
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
                    fontSize = 13.sp,
                    lineHeight = 16.sp
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
}
