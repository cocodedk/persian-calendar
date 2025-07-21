package com.cocode.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

class CalendarViewModel(
    private val eventDao: EventDao
) : ViewModel() {
    // MutableStateFlow to hold the current Gregorian date
    private val _gregorianDate = kotlinx.coroutines.flow.MutableStateFlow(LocalDate.now())
    // MutableStateFlow to hold the current calendar mode (false for Gregorian, true for Jalali)
    private val _isJalaliCalendar = kotlinx.coroutines.flow.MutableStateFlow(false)

    // Expose an immutable LiveData for observers to observe the current Gregorian date
    val gregorianDate = _gregorianDate.asLiveData()
    // Expose an immutable LiveData for observers to observe the current calendar mode
    val isJalaliCalendar = _isJalaliCalendar.asLiveData()

    private val _showConverter = kotlinx.coroutines.flow.MutableStateFlow(false)
    val showConverter: StateFlow<Boolean> = _showConverter.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _showJalaliToGregorianConverter = kotlinx.coroutines.flow.MutableStateFlow(true)
    val showJalaliToGregorianConverter: StateFlow<Boolean> = _showJalaliToGregorianConverter.stateIn(viewModelScope, SharingStarted.Lazily, true)

    private val _showGregorianToJalaliConverter = kotlinx.coroutines.flow.MutableStateFlow(false)
    val showGregorianToJalaliConverter: StateFlow<Boolean> = _showGregorianToJalaliConverter.stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Event creation dialog state
    private val _showEventCreationDialog = kotlinx.coroutines.flow.MutableStateFlow(false)
    val showEventCreationDialog: StateFlow<Boolean> = _showEventCreationDialog.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _selectedDate = kotlinx.coroutines.flow.MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Event list dialog state
    private val _showEventListDialog = kotlinx.coroutines.flow.MutableStateFlow(false)
    val showEventListDialog: StateFlow<Boolean> = _showEventListDialog.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _eventListSelectedDate = kotlinx.coroutines.flow.MutableStateFlow<LocalDate?>(null)
    val eventListSelectedDate: StateFlow<LocalDate?> = _eventListSelectedDate.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Delete confirmation dialog state
    private val _showDeleteConfirmationDialog = kotlinx.coroutines.flow.MutableStateFlow(false)
    val showDeleteConfirmationDialog: StateFlow<Boolean> = _showDeleteConfirmationDialog.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _eventToDelete = kotlinx.coroutines.flow.MutableStateFlow<Event?>(null)
    val eventToDelete: StateFlow<Event?> = _eventToDelete.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Expose events as a StateFlow from the DAO
    val events = eventDao.getAllEvents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateGregorianDate(newDate: LocalDate) {
        _gregorianDate.value = newDate
    }

    fun toggleIsJalaliCalendar() {
        _isJalaliCalendar.value = !_isJalaliCalendar.value
    }

    fun toggleConverter() {
        _showConverter.value = !_showConverter.value
    }

    fun toggleJalaliToGregorianConverter() {
        Log.d("Converter", "toggleJalaliToGregorianConverter ${_showJalaliToGregorianConverter.value} ${_showGregorianToJalaliConverter.value}")
        _showJalaliToGregorianConverter.value = !_showJalaliToGregorianConverter.value
        _showGregorianToJalaliConverter.value = !_showGregorianToJalaliConverter.value
    }

    fun showEventCreationDialog(date: LocalDate) {
        _selectedDate.value = date
        _showEventCreationDialog.value = true
    }

    fun hideEventCreationDialog() {
        _showEventCreationDialog.value = false
        _selectedDate.value = null
    }

    fun showEventListDialog(date: LocalDate) {
        _eventListSelectedDate.value = date
        _showEventListDialog.value = true
    }

    fun hideEventListDialog() {
        _showEventListDialog.value = false
        _eventListSelectedDate.value = null
    }

    fun showEventCreationFromEventList(date: LocalDate) {
        _showEventListDialog.value = false
        _eventListSelectedDate.value = null
        showEventCreationDialog(date)
    }

    fun showDeleteConfirmationDialog(event: Event) {
        _eventToDelete.value = event
        _showDeleteConfirmationDialog.value = true
    }

    fun hideDeleteConfirmationDialog() {
        _showDeleteConfirmationDialog.value = false
        _eventToDelete.value = null
    }

    fun confirmDeleteEvent() {
        _eventToDelete.value?.let { event ->
            viewModelScope.launch {
                eventDao.deleteEvent(event)
            }
        }
        hideDeleteConfirmationDialog()
    }

    fun getEventsForDateOnly(date: LocalDate): StateFlow<List<Event>> {
        return eventDao.getEventsForDate(date.toString()).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun changeMonth(newYearMonth: YearMonth) {
        val now = LocalDate.now()
        _gregorianDate.value = if (newYearMonth.year == now.year && newYearMonth.monthValue == now.monthValue) {
            now
        } else {
            newYearMonth.atDay(1)
        }
    }

    fun changeYear(newYear: Int) {
        _gregorianDate.value = _gregorianDate.value.withYear(newYear).withDayOfMonth(1)
    }

    // --- Event operations using DAO ---
    fun addEvent(
        title: String,
        description: String? = null,
        startDate: LocalDate,
        endDate: LocalDate = startDate,
        color: String = "BLUE",
        isAllDay: Boolean = true
    ) {
        val event = Event(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            color = color,
            isAllDay = isAllDay
        )
        viewModelScope.launch {
            eventDao.insertEvent(event)
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
