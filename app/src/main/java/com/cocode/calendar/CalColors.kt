package com.cocode.calendar
import androidx.compose.ui.graphics.Color

/**
 * This class is used to define the color scheme for the calendar application.
 * It contains a set of predefined colors that are used throughout the application.
 * Each color is defined as a constant with a hexadecimal color code.
 */
class CalColors {
    companion object{
        val background = Color(0xFF025842)
        val text = Color(0xFFFFFFFF)
        val day_background = Color(0xFF019A64)
        val current_day_background = Color(0xFF43C7F9)
        val current_day_text = Color(0xFFFFFFFF)
        val day_text_dark = Color(0xFFEF4E58)
        val day_text_light = Color(0xFFFFFFFF)
        val weekday_text = Color(0xFF43C7F9)
        val weekend_text = Color(0xFFF05066)
    }
}