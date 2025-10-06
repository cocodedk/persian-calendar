package com.cocode.calendar.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

/**
 * HeaderComponents.kt - Facade for header and time display components.
 *
 * This file now acts as a simple facade that delegates to the specialized
 * header and time components for better maintainability and organization.
 */

// Re-export header components for backward compatibility
@Composable
fun HeaderSection() {
    com.cocode.calendar.components.header.HeaderSection()
}

@Composable
fun CalendarHeader() {
    com.cocode.calendar.components.header.CalendarHeader()
}

@Composable
fun DisplayTimeInIran() {
    com.cocode.calendar.components.time.DisplayTimeInIran()
}
