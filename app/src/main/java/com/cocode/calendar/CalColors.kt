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
        val prev_month_background = Color(0xFF019A64) // previously not_current_month_background
        val prev_month_text = Color(0xFFEF4E58) // previously not_current_month_text
        val next_month_background = Color(0xFFA4D3B3) // new color
        val next_month_text = Color(0xFF43C7F9) // new color
        val day_background = Color(0xFFFFFFFF)
        val current_day_background = Color(0xFF43C7F9)
        val current_day_text = Color(0xFFFFFFFF)

        val day_text_light = Color(0xFFFFFFFF)
        val weekday_text = Color(0xFF43C7F9)
        val weekend_text = Color(0xFFF05066)
        val border_color = Color(0xFFFFFFFF)
        val day_border = Color(0xFF025842)
        val week_border = Color(0xFF43C7F9)
        val button_background = Color(0xFF019A64)
        var active_text = Color(0xFFFFFFFF)
        var inactive_text = Color(0xFFA4D3B3)
    }
}