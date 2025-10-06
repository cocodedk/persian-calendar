package com.cocode.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocode.calendar.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.time.LocalDate

/**
 * ViewModel responsible for dialog state management.
 * Handles showing/hiding various dialogs and their associated state.
 */
class DialogManagementViewModel : ViewModel() {

    // Event creation dialog state
    private val _showEventCreationDialog = MutableStateFlow(false)
    val showEventCreationDialog: StateFlow<Boolean> = _showEventCreationDialog.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Event list dialog state
    private val _showEventListDialog = MutableStateFlow(false)
    val showEventListDialog: StateFlow<Boolean> = _showEventListDialog.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _eventListSelectedDate = MutableStateFlow<LocalDate?>(null)
    val eventListSelectedDate: StateFlow<LocalDate?> = _eventListSelectedDate.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Delete confirmation dialog state
    private val _showDeleteConfirmationDialog = MutableStateFlow(false)
    val showDeleteConfirmationDialog: StateFlow<Boolean> = _showDeleteConfirmationDialog.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _eventToDelete = MutableStateFlow<Event?>(null)
    val eventToDelete: StateFlow<Event?> = _eventToDelete.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Event editing dialog state
    private val _showEventEditDialog = MutableStateFlow(false)
    val showEventEditDialog: StateFlow<Boolean> = _showEventEditDialog.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _eventToEdit = MutableStateFlow<Event?>(null)
    val eventToEdit: StateFlow<Event?> = _eventToEdit.stateIn(viewModelScope, SharingStarted.Lazily, null)

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

    fun showEventEditDialog(event: Event) {
        _eventToEdit.value = event
        _showEventEditDialog.value = true
    }

    fun hideEventEditDialog() {
        _showEventEditDialog.value = false
        _eventToEdit.value = null
    }

    fun showDeleteConfirmationDialog(event: Event) {
        _eventToDelete.value = event
        _showDeleteConfirmationDialog.value = true
    }

    fun hideDeleteConfirmationDialog() {
        _showDeleteConfirmationDialog.value = false
        _eventToDelete.value = null
    }
}
