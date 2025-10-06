package com.cocode.calendar.components.pickers

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalendarViewModel
import utils.Strings
import java.time.LocalDate
import kotlin.math.max

/**
 * Utility functions for picker dialogs and calendar navigation.
 * Contains common logic for month/year selection and calendar conversion.
 */
object PickerUtils {

    /**
     * Gets the appropriate month names array based on calendar type.
     */
    fun getMonthNames(isJalaliCalendar: Boolean): Array<String> {
        return if (isJalaliCalendar) {
            Strings.Months.JALALI_ABBREVIATED.toTypedArray()
        } else {
            Strings.Months.GREGORIAN_ABBREVIATED.toTypedArray()
        }
    }

    /**
     * Gets the current month value based on calendar type.
     */
    fun getCurrentMonth(currentDate: LocalDate?, isJalaliCalendar: Boolean): Int {
        return if (isJalaliCalendar) {
            // Convert current Gregorian date to Jalali and get the Jalali month
            currentDate?.let { com.cocode.calendar.converter.GregorianToJalaliConverter.gregorianToJalali(it).monthValue } ?: 1
        } else {
            currentDate?.monthValue ?: 1
        }
    }

    /**
     * Gets the current year based on calendar type.
     */
    fun getCurrentYear(currentDate: LocalDate?, isJalaliCalendar: Boolean): Int {
        return if (isJalaliCalendar) {
            // Get current Jalali year
            com.cocode.calendar.converter.GregorianToJalaliConverter.gregorianToJalali(currentDate ?: LocalDate.now()).year
        } else {
            currentDate?.year ?: LocalDate.now().year
        }
    }

    /**
     * Converts a list of Gregorian years to display years based on calendar type.
     */
    fun convertYearsForDisplay(gregorianYears: List<Int>, isJalaliCalendar: Boolean): List<Int> {
        return if (isJalaliCalendar) {
            // Convert Gregorian years to Jalali years for display
            gregorianYears.map { gregorianYear ->
                val tempDate = LocalDate.of(gregorianYear, 6, 15) // Use middle of year for conversion
                com.cocode.calendar.converter.GregorianToJalaliConverter.gregorianToJalali(tempDate).year
            }
        } else {
            gregorianYears
        }
    }

    /**
     * Calculates the scroll position for the current year in a year picker.
     */
    fun calculateScrollPosition(currentYearIndex: Int): Int {
        return maxOf(0, currentYearIndex - 2) // Show current year with some context above
    }

    /**
     * Validates a month selection (1-12).
     */
    fun isValidMonth(month: Int): Boolean {
        return month in 1..12
    }

    /**
     * Validates a year selection within reasonable bounds.
     */
    fun isValidYear(year: Int): Boolean {
        return year in 1900..2100
    }

    /**
     * Gets a default year range for picker dialogs.
     */
    fun getDefaultYearRange(): Pair<Int, Int> {
        return 1900 to 2100
    }

    /**
     * Creates a LazyListState for year picker with proper initial scroll position.
     */
    @Composable
    fun createYearPickerListState(
        displayYears: List<Int>,
        currentDisplayYear: Int
    ): LazyListState {
        val currentYearIndex = displayYears.indexOf(currentDisplayYear)
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        // Auto-scroll to current year when dialog opens
        LaunchedEffect(Unit) {
            if (currentYearIndex >= 0) {
                listState.scrollToItem(calculateScrollPosition(currentYearIndex))
            }
        }

        return listState
    }
}
