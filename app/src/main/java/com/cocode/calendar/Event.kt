package com.cocode.calendar

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class Event(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null,
    val startDate: String, // ISO date string
    val endDate: String,   // ISO date string
    val color: String = "BLUE",
    val isAllDay: Boolean = true,
    // New fields for repetition
    val isRepeating: Boolean = false,
    val repetitionType: String = "NONE", // NONE, YEARLY
    val originalDate: String? = null, // For repeating events, tracks the original date
    val repetitionEndDate: String? = null // Optional end date for repetition
) {
    fun occursOn(date: java.time.LocalDate): Boolean {
        val start = java.time.LocalDate.parse(startDate)
        val end = java.time.LocalDate.parse(endDate)

        // Check if it's a direct date match
        val directMatch = !date.isBefore(start) && !date.isAfter(end)

        // If it's a repeating yearly event, check for yearly occurrences
        if (isRepeating && repetitionType == "YEARLY" && !directMatch) {
            val originalStartDate = originalDate?.let { java.time.LocalDate.parse(it) } ?: start
            val repetitionEnd = repetitionEndDate?.let { java.time.LocalDate.parse(it) }

            // Check if we're within the repetition period
            if (repetitionEnd != null && date.isAfter(repetitionEnd)) {
                return false
            }

            // Check if the month and day match for yearly repetition
            if (date.monthValue == originalStartDate.monthValue &&
                date.dayOfMonth == originalStartDate.dayOfMonth) {
                // Make sure it's not before the original date
                return !date.isBefore(originalStartDate)
            }
        }

        return directMatch
    }

    companion object {
        const val REPETITION_NONE = "NONE"
        const val REPETITION_YEARLY = "YEARLY"
    }
}
