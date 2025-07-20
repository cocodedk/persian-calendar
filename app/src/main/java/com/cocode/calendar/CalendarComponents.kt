package com.cocode.calendar

/**
 * Re-export file for backward compatibility.
 * All calendar components have been refactored into organized subfolders:
 *
 * - components.HeaderComponents: CalendarHeader, DisplayTimeInIran
 * - components.GridComponents: WeekDaysHeader, DayOfWeekBox, CalendarGrid, WeekRow, DayBox
 * - components.ControlComponents: CalControls, TodayButton, DateConverterToggleButton, CalendarToggleButton
 * - components.NavigationComponents: CalendarNavigation, MonthPickerDialog, YearPickerDialog
 *
 * Import directly from the specific component files for better organization:
 * import com.cocode.calendar.components.CalendarHeader
 * import com.cocode.calendar.components.CalendarGrid
 * etc.
 */

// Re-export all components for any files still using the old import structure
import com.cocode.calendar.components.CalendarHeader as CalendarHeader
import com.cocode.calendar.components.DisplayTimeInIran as DisplayTimeInIran
import com.cocode.calendar.components.WeekDaysHeader as WeekDaysHeader
import com.cocode.calendar.components.DayOfWeekBox as DayOfWeekBox
import com.cocode.calendar.components.CalendarGrid as CalendarGrid
import com.cocode.calendar.components.WeekRow as WeekRow
import com.cocode.calendar.components.DayBox as DayBox
import com.cocode.calendar.components.CalControls as CalControls
import com.cocode.calendar.components.TodayButton as TodayButton
import com.cocode.calendar.components.DateConverterToggleButton as DateConverterToggleButton
import com.cocode.calendar.components.CalendarToggleButton as CalendarToggleButton
import com.cocode.calendar.components.CalendarNavigation as CalendarNavigation
import com.cocode.calendar.components.MonthPickerDialog as MonthPickerDialog
import com.cocode.calendar.components.YearPickerDialog as YearPickerDialog
