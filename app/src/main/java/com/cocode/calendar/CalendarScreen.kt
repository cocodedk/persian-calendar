package com.cocode.calendar

import androidx.compose.runtime.Composable

/**
 * CalendarScreen.kt - Facade for screen-level components.
 *
 * This file now acts as a simple facade that delegates to the specialized
 * screen components for better maintainability and organization.
 *
 * The implementation has been split into:
 * - screens/CalendarApp.kt for app-level components
 * - screens/MainCalendarScreen.kt for main screen layout
 */

// Re-export app component for backward compatibility
@Composable
fun CalendarApp() {
    com.cocode.calendar.screens.CalendarApp()
}

// Re-export main screen component for backward compatibility
@Composable
fun CalendarScreen() {
    com.cocode.calendar.screens.CalendarScreen()
}

