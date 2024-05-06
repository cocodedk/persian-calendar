package utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.WeekFields
import java.util.Locale
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

}