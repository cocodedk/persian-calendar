package utils

import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAccessor
import java.util.Locale

/**
 * Dates and times as the language of the phone writes them: order, month names and separators
 * come from the locale, not from a fixed pattern. Digits stay 0-9 in every language, because
 * java.time uses its standard decimal style (a Persian-language phone shows 2026, not ۲۰۲۶).
 *
 * Pure Kotlin. The month-and-year and time formats need the phone's best pattern for a skeleton
 * (`android.text.format.DateFormat.getBestDateTimePattern`), which the caller passes in as
 * [BestPattern], so tests can use their own.
 */
object DateFormats {

    /** Returns the locale's pattern for a skeleton such as "yMMMM" or "Hms". */
    fun interface BestPattern {
        fun of(locale: Locale, skeleton: String): String
    }

    /** A day, with the locale's medium style: "Oct 8, 2026" in English, "8. okt. 2026" in Danish. */
    fun mediumDate(date: TemporalAccessor, locale: Locale): String =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale).format(date)

    /** A month and a year: "October 2026" in English and Danish ("oktober 2026"). */
    fun monthYear(date: TemporalAccessor, locale: Locale, bestPattern: BestPattern): String =
        format(date, locale, bestPattern.of(locale, "yMMMM"), fallback = "MMMM yyyy")

    /**
     * A time with seconds. [is24Hour] follows the phone's clock setting: "12:57:36" or
     * "12:57:36 PM" in English, "12.57.36" in Danish.
     */
    fun time(time: TemporalAccessor, locale: Locale, is24Hour: Boolean, bestPattern: BestPattern): String =
        format(time, locale, bestPattern.of(locale, if (is24Hour) "Hms" else "hms"), fallback = "HH:mm:ss")

    private fun format(value: TemporalAccessor, locale: Locale, pattern: String, fallback: String): String =
        try {
            DateTimeFormatter.ofPattern(pattern, locale).format(value)
        } catch (e: RuntimeException) {
            // A pattern java.time does not understand (IllegalArgumentException) or cannot fill
            // from this value (DateTimeException): use the plain one rather than fail
            DateTimeFormatter.ofPattern(fallback, locale).format(value)
        }
}
