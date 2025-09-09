package com.cocode.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cocode.calendar.ui.theme.CalendarTheme

// Describe the application
// This is a simple calendar app that displays a calendar view
// with the ability to toggle between Gregorian and Persian (Jalali) calendars.

/**
 * This is the main activity for the calendar application.
 *
 * @property MainActivity This class extends ComponentActivity, which is a base class for activities that use the new androidx APIs.
 * @constructor Creates an instance of MainActivity.
 */
class MainActivity : ComponentActivity() {

    /**
     * This function is called when the activity is starting.
     * This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply the theme for the calendar application
            CalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CalColors.background
                ) {
                    // Call the main screen composable of the calendar application
                    CalendarApp()
                }
            }
        }
    }
}
