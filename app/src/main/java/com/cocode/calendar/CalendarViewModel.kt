package com.cocode.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth

/**
 * This class represents the ViewModel for the Calendar application.
 * It holds the state for the current Gregorian date and the calendar mode (Gregorian or Jalali).
 *
 * @property CalendarViewModel This class extends ViewModel, which is designed to store and manage
 * UI-related data in a lifecycle conscious way.
 * @constructor Creates an instance of CalendarViewModel.
 */
class CalendarViewModel : ViewModel() {
    // MutableStateFlow to hold the current Gregorian date
    private val _gregorianDate = MutableStateFlow(LocalDate.now())
    // MutableStateFlow to hold the current calendar mode (false for Gregorian, true for Jalali)
    private val _isJalaliCalendar = MutableStateFlow(false)

    // Expose an immutable LiveData for observers to observe the current Gregorian date
    val gregorianDate = _gregorianDate.asLiveData()
    // Expose an immutable LiveData for observers to observe the current calendar mode
    val isJalaliCalendar = _isJalaliCalendar.asLiveData()

    private val _showConverter = MutableStateFlow(false)
    val showConverter: StateFlow<Boolean> = _showConverter.asStateFlow()

    private val _showJalaliToGregorianConverter = MutableStateFlow(true)
    val showJalaliToGregorianConverter: StateFlow<Boolean> = _showJalaliToGregorianConverter.asStateFlow()

    private val _showGregorianToJalaliConverter = MutableStateFlow(false)
    val showGregorianToJalaliConverter: StateFlow<Boolean> = _showGregorianToJalaliConverter.asStateFlow()

    /**
     * Updates the current Gregorian date.
     *
     * @param newDate The new Gregorian date to set.
     */
    fun updateGregorianDate(newDate: LocalDate) {
        _gregorianDate.value = newDate
    }

    /**
     * Toggles the current calendar mode.
     * If the current mode is Gregorian, it changes to Jalali, and vice versa.
     */
    fun toggleIsJalaliCalendar() {
        _isJalaliCalendar.value = !_isJalaliCalendar.value
    }

    fun toggleConverter() {
        _showConverter.value =!_showConverter.value
    }

    fun toggleJalaliToGregorianConverter() {
        Log.d("Converter", "toggleJalaliToGregorianConverter ${_showJalaliToGregorianConverter.value} ${_showGregorianToJalaliConverter.value}")
        _showJalaliToGregorianConverter.value = !_showJalaliToGregorianConverter.value
        _showGregorianToJalaliConverter.value = !_showGregorianToJalaliConverter.value
    }

    /**
     * Changes the current month in the calendar.
     *
     * This function updates the current Gregorian date to either the current date (if the new month
     * is the current month) or the first day of the new month. It ensures that when changing to
     * the current month, the date is set to today's date rather than the first day of the month.
     *
     * @param newYearMonth The new year and month to set the calendar to. This parameter
     *                     determines which month the calendar will display.
     */
    fun changeMonth(newYearMonth: YearMonth) {
        val now = LocalDate.now()
        _gregorianDate.value = if (newYearMonth.year == now.year && newYearMonth.monthValue == now.monthValue) {
            now
        } else {
            newYearMonth.atDay(1)
        }
    }

    /**
     * Changes the year of the current Gregorian date.
     *
     * This function updates the current Gregorian date to the first day of the specified year.
     * It maintains the current month but resets the day to the first of that month.
     *
     * @param newYear The year to set the current date to.
     */
    fun changeYear(newYear: Int) {
        _gregorianDate.value = _gregorianDate.value.withYear(newYear).withDayOfMonth(1)
    }
}
