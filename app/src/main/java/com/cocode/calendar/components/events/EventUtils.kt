package com.cocode.calendar.components.events

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cocode.calendar.CalColors
import com.cocode.calendar.Event
import java.time.LocalDate

/**
 * Utility functions for event-related operations and formatting.
 * This class contains helper methods for event display and management.
 */
object EventUtils {

    /**
     * Gets the appropriate color for an event based on its properties.
     *
     * @param event The event to get color for
     * @return Color to use for displaying the event
     */
    fun getEventDisplayColor(event: Event): Color {
        return when (event.color.uppercase()) {
            "RED" -> Color.Red
            "GREEN" -> Color.Green
            "BLUE" -> Color.Blue
            "YELLOW" -> Color.Yellow
            "PURPLE" -> Color(0xFF9C27B0)
            "ORANGE" -> Color(0xFFFF9800)
            else -> CalColors.button_background
        }
    }

    /**
     * Formats event duration for display.
     *
     * @param event The event to format
     * @return Formatted duration string
     */
    fun formatEventDuration(event: Event): String {
        return if (event.isAllDay) {
            "All Day"
        } else {
            val startDate = LocalDate.parse(event.startDate)
            val endDate = LocalDate.parse(event.endDate)

            if (startDate == endDate) {
                "Single Day"
            } else {
                val days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
                "${days} Days"
            }
        }
    }

    /**
     * Gets the repetition description for an event.
     *
     * @param event The event to get repetition info for
     * @return Formatted repetition description
     */
    fun getRepetitionDescription(event: Event): String {
        return when {
            !event.isRepeating -> "One-time event"
            event.repetitionType == "DAILY" -> "Repeats daily"
            event.repetitionType == "WEEKLY" -> "Repeats weekly"
            event.repetitionType == "MONTHLY" -> "Repeats monthly"
            event.repetitionType == "YEARLY" -> {
                if (event.repetitionEndDate != null) {
                    "Repeats yearly until ${LocalDate.parse(event.repetitionEndDate!!).year}"
                } else {
                    "Repeats yearly"
                }
            }
            else -> "Unknown repetition"
        }
    }

    /**
     * Checks if an event occurs on a specific date.
     *
     * @param event The event to check
     * @param date The date to check against
     * @return True if the event occurs on the given date
     */
    fun isEventOnDate(event: Event, date: LocalDate): Boolean {
        val startDate = LocalDate.parse(event.startDate)
        val endDate = LocalDate.parse(event.endDate)

        return date in startDate..endDate
    }

    /**
     * Gets events that occur on a specific date.
     *
     * @param events List of events to filter
     * @param date The date to filter by
     * @return List of events that occur on the given date
     */
    fun getEventsForDate(events: List<Event>, date: LocalDate): List<Event> {
        return events.filter { isEventOnDate(it, date) }
    }
}
