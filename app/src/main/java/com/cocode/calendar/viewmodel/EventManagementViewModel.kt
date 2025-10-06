package com.cocode.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocode.calendar.Event
import com.cocode.calendar.EventDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

/**
 * ViewModel responsible for event management operations.
 * Handles event CRUD operations and event data access.
 */
class EventManagementViewModel(
    private val eventDao: EventDao
) : ViewModel() {

    // Expose events as a StateFlow from the DAO
    val events = eventDao.getAllEvents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- Event operations using DAO ---
    fun addEvent(
        title: String,
        description: String? = null,
        startDate: LocalDate,
        endDate: LocalDate = startDate,
        color: String = "BLUE",
        isAllDay: Boolean = true,
        isRepeating: Boolean = false,
        repetitionType: String = "NONE",
        repetitionEndDate: LocalDate? = null
    ) {
        val event = Event(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            color = color,
            isAllDay = isAllDay,
            isRepeating = isRepeating,
            repetitionType = repetitionType,
            originalDate = if (isRepeating) startDate.toString() else null,
            repetitionEndDate = repetitionEndDate?.toString()
        )
        viewModelScope.launch {
            eventDao.insertEvent(event)
        }
    }

    fun updateEvent(
        event: Event,
        title: String,
        description: String? = null,
        startDate: LocalDate,
        endDate: LocalDate = startDate,
        color: String = "BLUE",
        isAllDay: Boolean = true,
        isRepeating: Boolean = false,
        repetitionType: String = "NONE",
        repetitionEndDate: LocalDate? = null
    ) {
        val updatedEvent = event.copy(
            title = title,
            description = description,
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            color = color,
            isAllDay = isAllDay,
            isRepeating = isRepeating,
            repetitionType = repetitionType,
            originalDate = if (isRepeating && event.originalDate == null) startDate.toString() else event.originalDate,
            repetitionEndDate = repetitionEndDate?.toString()
        )
        viewModelScope.launch {
            eventDao.updateEvent(updatedEvent)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventDao.deleteEvent(event)
        }
    }

    fun getEventsForDate(date: LocalDate): StateFlow<List<Event>> {
        return eventDao.getEventsForDate(date.toString()).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun getEventsForMonth(yearMonth: YearMonth): StateFlow<List<Event>> {
        val startOfMonth = yearMonth.atDay(1).toString()
        val endOfMonth = yearMonth.atEndOfMonth().toString()
        // This is a simplified version; for more complex queries, add a DAO method
        return eventDao.getAllEvents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
}
