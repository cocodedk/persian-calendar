package utils
/**
 * Contains string constants and lists used throughout the application.
 *
 * This object is organized into nested objects, each grouping related strings:
 * - [Calendar]: Strings related to calendar display and navigation.
 * - [DateFormat]: Date and time format strings.
 * - [Converter]: Strings for date conversion functionality.
 * - [Months]: Lists of month names in Jalali and Gregorian calendars.
 * - [Error]: Error message strings.
 */

object Strings {
    object Calendar {
        const val IRAN_TIME = "Iran time: "
        val DAYS_OF_WEEK = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        const val SUN = "Sun"
        const val MON = "Mon"
        const val TUE = "Tue"
        const val WED = "Wed"
        const val THU = "Thu"
        const val FRI = "Fri"
        const val SAT = "Sat"
        const val NEXT_MONTH = "Next month"
        const val PREVIOUS_MONTH = "Previous month"
        const val NEXT_YEAR = "Next year"
        const val PREVIOUS_YEAR = "Previous year"
        const val WEEK = "week "
        const val TODAY = "Today"
        const val TOGGLE_CALENDAR = "Toggle Calendar"
        const val TOGGLE_CONVERTER = "Toggle Converter"
        const val JALALI = "Jalali"
        const val GREGORIAN = "Gregorian"
        object Controls {
            const val NEXT_MONTH = "+M"
            const val NEXT_YEAR = "+Y"
            const val PREVIOUS_MONTH = "M-"
            const val PREVIOUS_YEAR = "-Y"
        }
    }
    
    object DateFormat {
        const val TIME_FORMAT = "HH:mm:ss"
        const val MONTH_YEAR_FORMAT = "MMMM yyyy"
    }

    object Converter {
        const val JALALI_TO_GREGORIAN = "Jalali to Gregorian"
        const val GREGORIAN_TO_JALALI = "Gregorian to Jalali"
        const val YEAR = "Year"
        const val MONTH = "Month"
        const val DAY = "Day"
        const val CONVERT = "Convert"
        const val CONVERT_TO_JALALI = "Convert to Jalali"
        const val CONVERT_TO_GREGORIAN = "Convert to Gregorian"
        const val SWITCH_TO_JALALI_TO_GREGORIAN = "Switch to Jalali to Gregorian Converter"
        const val SWITCH_TO_GREGORIAN_TO_JALALI = "Switch to Gregorian to Jalali Converter"
        const val ENTER_GREGORIAN_DATE = "Enter Gregorian Date"
        const val ENTER_JALALI_DATE = "Enter Jalali Date"
    }

    object Months {
        val JALALI = listOf(
            "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
            "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
        )
        val GREGORIAN = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    }

    object Error {
        const val INVALID_DATE = "Invalid date"
        const val WRONG = "Something is wrong"
    }
}