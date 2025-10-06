package com.cocode.calendar.components

import androidx.compose.runtime.Composable

/**
 * NavigationComponents.kt - Facade for navigation and picker dialog components.
 *
 * This file now acts as a simple facade that delegates to the specialized
 * navigation and picker components for better maintainability and organization.
 */

// Re-export navigation components for backward compatibility
@Composable
fun CalendarNavigation() {
    com.cocode.calendar.components.navigation.CalendarNavigation()
}

@Composable
fun MonthPickerDialog(
    onDismiss: () -> Unit,
    onMonthSelected: (Int) -> Unit
) {
    com.cocode.calendar.components.pickers.MonthPickerDialog(
        onDismiss = onDismiss,
        onMonthSelected = onMonthSelected
    )
}

@Composable
fun YearPickerDialog(
    onDismiss: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    com.cocode.calendar.components.pickers.YearPickerDialog(
        onDismiss = onDismiss,
        onYearSelected = onYearSelected
    )
}
