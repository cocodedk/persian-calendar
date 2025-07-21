package com.cocode.calendar

// Re-export the main component from the converter package for backwards compatibility
import androidx.compose.runtime.Composable
import com.cocode.calendar.converter.CalendarConverterBox as ConverterBox

@Composable
fun CalendarConverterBox() {
    ConverterBox()
}
