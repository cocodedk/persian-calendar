package com.cocode.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.cocode.calendar.viewmodel.CalendarNavigationViewModel
import com.cocode.calendar.viewmodel.DialogManagementViewModel
import com.cocode.calendar.viewmodel.EventManagementViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * Main CalendarViewModel that coordinates between specialized ViewModels.
 * Acts as a facade providing a unified interface while delegating to focused ViewModels.
 */
class CalendarViewModel(
    private val eventDao: EventDao,
    private val navigationViewModel: CalendarNavigationViewModel = CalendarNavigationViewModel(),
    private val eventManagementViewModel: EventManagementViewModel = EventManagementViewModel(eventDao),
    private val dialogManagementViewModel: DialogManagementViewModel = DialogManagementViewModel()
) : ViewModel() {

    // Delegate state flows from specialized ViewModels
    val gregorianDate = navigationViewModel.gregorianDate
    val isJalaliCalendar = navigationViewModel.isJalaliCalendar
    val showConverter = navigationViewModel.showConverter
    val showJalaliToGregorianConverter = navigationViewModel.showJalaliToGregorianConverter
    val showGregorianToJalaliConverter = navigationViewModel.showGregorianToJalaliConverter

    val events = eventManagementViewModel.events

    val showEventCreationDialog = dialogManagementViewModel.showEventCreationDialog
    val selectedDate = dialogManagementViewModel.selectedDate
    val showEventListDialog = dialogManagementViewModel.showEventListDialog
    val eventListSelectedDate = dialogManagementViewModel.eventListSelectedDate
    val showDeleteConfirmationDialog = dialogManagementViewModel.showDeleteConfirmationDialog
    val eventToDelete = dialogManagementViewModel.eventToDelete
    val showEventEditDialog = dialogManagementViewModel.showEventEditDialog
    val eventToEdit = dialogManagementViewModel.eventToEdit

    // Navigation functions - delegate to CalendarNavigationViewModel
    fun updateGregorianDate(newDate: LocalDate) {
        navigationViewModel.updateGregorianDate(newDate)
    }

    fun toggleIsJalaliCalendar() {
        navigationViewModel.toggleIsJalaliCalendar()
    }

    fun toggleConverter() {
        navigationViewModel.toggleConverter()
    }

    fun toggleJalaliToGregorianConverter() {
        Log.d("Converter", "toggleJalaliToGregorianConverter ${navigationViewModel.showJalaliToGregorianConverter.value} ${navigationViewModel.showGregorianToJalaliConverter.value}")
        navigationViewModel.toggleJalaliToGregorianConverter()
    }

    // Dialog functions - delegate to DialogManagementViewModel
    fun showEventCreationDialog(date: LocalDate) {
        dialogManagementViewModel.showEventCreationDialog(date)
    }

    fun hideEventCreationDialog() {
        dialogManagementViewModel.hideEventCreationDialog()
    }

    fun showEventListDialog(date: LocalDate) {
        dialogManagementViewModel.showEventListDialog(date)
    }

    fun hideEventListDialog() {
        dialogManagementViewModel.hideEventListDialog()
    }

    fun showEventCreationFromEventList(date: LocalDate) {
        dialogManagementViewModel.showEventCreationFromEventList(date)
    }

    fun showEventEditDialog(event: Event) {
        dialogManagementViewModel.showEventEditDialog(event)
    }

    fun hideEventEditDialog() {
        dialogManagementViewModel.hideEventEditDialog()
    }

    fun showDeleteConfirmationDialog(event: Event) {
        dialogManagementViewModel.showDeleteConfirmationDialog(event)
    }

    fun hideDeleteConfirmationDialog() {
        dialogManagementViewModel.hideDeleteConfirmationDialog()
    }

    // Event deletion function - delegate to EventManagementViewModel
    fun confirmDeleteEvent() {
        dialogManagementViewModel.eventToDelete.value?.let { event ->
            eventManagementViewModel.deleteEvent(event)
        }
        dialogManagementViewModel.hideDeleteConfirmationDialog()
    }

    // Event retrieval functions - delegate to EventManagementViewModel
    fun getEventsForDateOnly(date: LocalDate): StateFlow<List<Event>> {
        return eventManagementViewModel.getEventsForDate(date)
    }

    fun changeMonth(newYearMonth: YearMonth) {
        navigationViewModel.changeMonth(newYearMonth)
    }

    fun changeYear(newYear: Int) {
        navigationViewModel.changeYear(newYear)
    }

    // Event operations - delegate to EventManagementViewModel
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
        eventManagementViewModel.addEvent(
            title, description, startDate, endDate, color, isAllDay, isRepeating, repetitionType, repetitionEndDate
        )
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
        eventManagementViewModel.updateEvent(
            event, title, description, startDate, endDate, color, isAllDay, isRepeating, repetitionType, repetitionEndDate
        )
    }

    fun getEventsForDate(date: LocalDate): StateFlow<List<Event>> {
        return eventManagementViewModel.getEventsForDate(date)
    }

    fun getEventsForMonth(yearMonth: YearMonth): StateFlow<List<Event>> {
        return eventManagementViewModel.getEventsForMonth(yearMonth)
    }
}
