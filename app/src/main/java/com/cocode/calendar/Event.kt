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
    val isAllDay: Boolean = true
) {
    fun occursOn(date: java.time.LocalDate): Boolean {
        val start = java.time.LocalDate.parse(startDate)
        val end = java.time.LocalDate.parse(endDate)
        return !date.isBefore(start) && !date.isAfter(end)
    }
}
