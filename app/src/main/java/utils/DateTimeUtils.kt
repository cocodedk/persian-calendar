package utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.abs
import java.time.Period

object DateTimeUtils {

    /**
     * Retrieves the current date and time in Iran.
     *
     * @return A ZonedDateTime object representing the current date and time in Iran.
     */
    fun getCurrentTimeInIran(): ZonedDateTime {
        // Define the time zone for Iran. Iran Standard Time (IRST) is usually UTC+3:30
        val iranZoneId = ZoneId.of("Asia/Tehran")
        // Get the current date and time in Iran
        return ZonedDateTime.now(iranZoneId)
    }


    /**
     * Adjusts the current date to match the date in Iran's timezone.
     *
     * This function first retrieves the device's current timezone and the current date and time in Iran.
     * It then converts Iran's current time to the device's timezone.
     * If the local date is still behind Iran's date, it returns Iran's current date.
     * Otherwise, it returns the local date.
     *
     * @return A LocalDate object representing the adjusted date.
     */
    fun adjustDateForDeviceTimeZone(): LocalDate {
        // Get the device's current time zone
        val deviceZoneId = ZoneId.systemDefault()
        // Get the current time in Iran
        val currentTimeInIran = getCurrentTimeInIran()
        // Convert Iran's current time to the local time zone
        val deviceTime = currentTimeInIran.withZoneSameInstant(deviceZoneId)

        // Check if the local time has not yet reached the current day in Iran
        return if (deviceTime.toLocalDate().isBefore(currentTimeInIran.toLocalDate())) {
            // If the local date is still behind Iran's date, use Iran's current date
            currentTimeInIran.toLocalDate()
        } else {
            // Otherwise, use the local date and time
            deviceTime.toLocalDate()
        }
    }

    /**
     * Retrieves the week number for the current date.
     *
     * @return An integer representing the week number.
     */
    fun getCurrentWeekNumber(date: LocalDate): Int {
        // Get the week fields for the current locale
        val weekFields = WeekFields.of(Locale.getDefault())
        // Get the week number for the current date
        return date.get(weekFields.weekOfWeekBasedYear())
    }

    /**
     * Calculates the number of days between a given Gregorian date and the current date.
     *
     * @param year The year of the given date.
     * @param month The month of the given date (1-12).
     * @param day The day of the given date.
     * @return A Pair containing:
     *         - The absolute number of days between the given date and today.
     *         - A Boolean indicating whether the given date is in the future (true) or past (false).
     * @throws IllegalArgumentException if the provided date is invalid.
     */
    fun daysFromGregorianDateToNow(year: Int, month: Int, day: Int): Pair<Long, Boolean> {
        val givenDate = try {
            LocalDate.of(year, month, day)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid date provided", e)
        }

        val today = LocalDate.now()
        val daysBetween = abs(ChronoUnit.DAYS.between(givenDate, today))
        val isFuture = givenDate.isAfter(today)

        return Pair(daysBetween, isFuture)
    }

    /**
     * Calculates the period (years, months, days) between a given Gregorian date and the current date.
     *
     * @param year The year of the given date.
     * @param month The month of the given date (1-12).
     * @param day The day of the given date.
     * @return A Triple containing:
     *         - The number of years between the given date and today.
     *         - The number of months between the given date and today (excluding years).
     *         - The number of days between the given date and today (excluding years and months).
     *         - A Boolean indicating whether the given date is in the future (true) or past (false).
     * @throws IllegalArgumentException if the provided date is invalid.
     */
    fun periodFromGregorianDateToNow(year: Int, month: Int, day: Int): Pair<Triple<Int, Int, Int>, Boolean> {
        val givenDate = try {
            LocalDate.of(year, month, day)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid date provided", e)
        }

        val today = LocalDate.now()
        val period = if (givenDate.isBefore(today)) {
            Period.between(givenDate, today)
        } else {
            Period.between(today, givenDate)
        }

        val isFuture = givenDate.isAfter(today)

        return Pair( Triple(
            period.years,
            period.months,
            period.days
            ),
            isFuture
        )
    }
}