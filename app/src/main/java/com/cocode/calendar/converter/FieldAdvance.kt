package com.cocode.calendar.converter

/**
 * Decides when a date field is finished, so focus moves on only when another digit could not
 * be typed. Pure Kotlin, no Android classes.
 */
object FieldAdvance {
    const val YEAR_DIGITS = 4
    const val MONTH_MAX = 12
    const val DAY_MAX = 31

    /**
     * True when [text] is a number in 1..[max] that no further digit could extend: it has as many
     * digits as [max], or ten times its value is already above [max]. For months (max 12) that is
     * two digits or a first digit of 2-9; for days (max 31) two digits or a first digit of 4-9.
     */
    fun isFull(text: String, max: Int): Boolean {
        val number = text.toIntOrNull() ?: return false
        return number in 1..max && (text.length >= max.toString().length || number * 10 > max)
    }
}
