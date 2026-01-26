package com.cocode.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.time.LocalDate
import java.time.YearMonth

/**
 * ViewModel responsible for calendar navigation state and operations.
 * Handles date navigation, month/year changes, and calendar type switching.
 */
class CalendarNavigationViewModel : ViewModel() {

    // MutableStateFlow to hold the current Gregorian date
    private val _gregorianDate = MutableStateFlow(LocalDate.now())

    // MutableStateFlow to hold the current calendar mode (false for Gregorian, true for Jalali)
    private val _isJalaliCalendar = MutableStateFlow(false)

    // Expose an immutable LiveData for observers to observe the current Gregorian date
    val gregorianDate = _gregorianDate.asLiveData()

    // Expose an immutable LiveData for observers to observe the current calendar mode
    val isJalaliCalendar = _isJalaliCalendar.asLiveData()

    private val _showConverter = MutableStateFlow(false)
    val showConverter: StateFlow<Boolean> = _showConverter.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _showJalaliToGregorianConverter = MutableStateFlow(true)
    val showJalaliToGregorianConverter: StateFlow<Boolean> = _showJalaliToGregorianConverter.stateIn(viewModelScope, SharingStarted.Lazily, true)

    private val _showGregorianToJalaliConverter = MutableStateFlow(false)
    val showGregorianToJalaliConverter: StateFlow<Boolean> = _showGregorianToJalaliConverter.stateIn(viewModelScope, SharingStarted.Lazily, false)

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
        _showJalaliToGregorianConverter.value = !_showJalaliToGregorianConverter.value
        _showGregorianToJalaliConverter.value = !_showJalaliToGregorianConverter.value
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
}
