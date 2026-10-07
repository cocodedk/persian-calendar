package utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DateFormatsTest {

    private val english = Locale.forLanguageTag("en")
    private val danish = Locale.forLanguageTag("da-DK")
    private val persian = Locale.forLanguageTag("fa-IR")
    private val day = LocalDate.of(2026, 10, 8)
    private val clock = ZonedDateTime.of(2026, 10, 8, 12, 57, 36, 0, ZoneId.of("Asia/Tehran"))

    // The phone's best pattern for a skeleton, as Android gives it for these languages
    private val patterns = DateFormats.BestPattern { locale, skeleton ->
        when (locale.language to skeleton) {
            "en" to "yMMMM", "da" to "yMMMM", "fa" to "yMMMM" -> "MMMM y"
            "en" to "Hms" -> "HH:mm:ss"
            "en" to "hms" -> "h:mm:ss a"
            "da" to "Hms" -> "HH.mm.ss"
            else -> error("no pattern for $locale $skeleton")
        }
    }

    @Test
    fun `should write a day in English order`() {
        assertEquals("Oct 8, 2026", DateFormats.mediumDate(day, english))
    }

    @Test
    fun `should write a day in Danish order`() {
        assertEquals("8. okt. 2026", DateFormats.mediumDate(day, danish))
    }

    @Test
    fun `should keep digits 0-9 for a Persian-language phone`() {
        val text = DateFormats.mediumDate(day, persian)
        val digits = text.filter { it.isDigit() }
        assertTrue(text, digits.isNotEmpty())
        assertTrue(text, digits.all { it in '0'..'9' })
    }

    @Test
    fun `should write the month and year in English and Danish`() {
        assertEquals("October 2026", DateFormats.monthYear(day, english, patterns))
        assertEquals("oktober 2026", DateFormats.monthYear(day, danish, patterns))
    }

    @Test
    fun `should keep digits 0-9 in the month and year on a Persian-language phone`() {
        val text = DateFormats.monthYear(day, persian, patterns)
        assertTrue(text, text.filter { it.isDigit() }.all { it in '0'..'9' })
    }

    @Test
    fun `should write the time in 24 hours, as the phone is set`() {
        assertEquals("12:57:36", DateFormats.time(clock, english, true, patterns))
        assertEquals("12.57.36", DateFormats.time(clock, danish, true, patterns))
    }

    @Test
    fun `should write the time in 12 hours when the phone is set to 12 hours`() {
        assertEquals("12:57:36 PM", DateFormats.time(clock, english, false, patterns))
    }

    @Test
    fun `should fall back to the plain pattern when the phone's pattern cannot be used`() {
        val broken = DateFormats.BestPattern { _, _ -> "QQQQ-bogus-[" }
        assertEquals("October 2026", DateFormats.monthYear(day, english, broken))
        assertEquals("12:57:36", DateFormats.time(clock, english, true, broken))
    }
}
