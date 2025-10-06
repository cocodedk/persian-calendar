package com.cocode.calendar.components

import androidx.compose.runtime.Composable

/**
 * EventCreationDialog.kt - Facade for event creation dialog components.
 *
 * This file now acts as a simple facade that delegates to the specialized
 * form and dialog components for better maintainability and organization.
 */

// Re-export the EventCreationDialog component for backward compatibility
@Composable
fun EventCreationDialog() {
    com.cocode.calendar.components.dialogs.EventCreationDialog()
}
