package com.cocode.calendar.converter

/** Decides when the Month field holds a whole month, so focus can move on to Day. */
object MonthEntry {

    /** "1" waits, because 10, 11 or 12 may follow; "2" to "9" and two digits are finished. */
    fun isComplete(text: String): Boolean {
        val month = text.toIntOrNull() ?: return false
        return month in 1..12 && (text.length == 2 || month >= 2)
    }
}
