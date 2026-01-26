package com.cocode.calendar.components

import androidx.compose.runtime.Composable

/**
 * EventListDialog.kt - Facade for event list dialog components.
 *
 * This file now acts as a simple facade that delegates to the specialized
 * event and dialog components for better maintainability and organization.
 */

// Re-export the EventListDialog component for backward compatibility
@Composable
fun EventListDialog() {
    com.cocode.calendar.components.dialogs.EventListDialog()
}

// Re-export EventItemCard for backward compatibility
@Composable
fun EventItemCard(
    event: com.cocode.calendar.Event,
    onEditEvent: (com.cocode.calendar.Event) -> Unit,
    onDeleteEvent: (com.cocode.calendar.Event) -> Unit
) {
    com.cocode.calendar.components.events.EventItemCard(
        event = event,
        onEditEvent = onEditEvent,
        onDeleteEvent = onDeleteEvent
    )
}
