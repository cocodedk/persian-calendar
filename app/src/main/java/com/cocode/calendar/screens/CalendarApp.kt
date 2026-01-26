package com.cocode.calendar.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.cocode.calendar.AppDatabase
import com.cocode.calendar.CalendarScreen
import com.cocode.calendar.CalendarViewModel
import java.time.LocalDate

/**
 * This Composable function represents the main application for the calendar.
 *
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose, a modern toolkit for building native Android UI.
 */
@Composable
fun CalendarApp() {
    val context = LocalContext.current
    val eventDao = remember { AppDatabase.getDatabase(context).eventDao() }
    val viewModel: CalendarViewModel = viewModel(
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
    viewModel.gregorianDate.observeAsState(initial = LocalDate.now())

    CalendarScreen()
}
