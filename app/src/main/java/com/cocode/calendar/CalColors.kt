package com.cocode.calendar
import androidx.compose.ui.graphics.Color

/**
 * This class is used to define the color scheme for the calendar application.
 * It contains a set of predefined colors that are used throughout the application.
 * Each color is defined as a constant with a hexadecimal color code.
 */
class CalColors {
    companion object {
        // Base colors
        private val Green = Color(0xFF025842)
        private val DarkGreen = Color(0xFF019A64)
        private val LightGreen = Color(0xFFA4D3B3)
        private val White = Color(0xFFFFFFFF)
        private val Blue = Color(0xFF43C7F9)
        private val Red = Color(0xFFF05066)
    
        // Assign colors to variables
        val background = Green
        val text = White
        val prev_month_background = DarkGreen
        val prev_month_text = White
        val next_month_background = LightGreen
        val next_month_text = White
        val day_background = White
        val current_day_background = Blue
        val current_day_text = White
        val weekday_text = Blue
        val weekend_text = Red
        val day_border = Green
        val button_background = DarkGreen
        var active_text = White
        var inactive_text = LightGreen
        var active_button_background = Blue
    }
}