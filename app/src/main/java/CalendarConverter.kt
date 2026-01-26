import com.cocode.calendar.converter.GregorianToJalaliConverter
import com.cocode.calendar.converter.JalaliToGregorianConverter
import com.cocode.calendar.models.JalaliDate
import com.cocode.calendar.models.JalaliMonth
import com.cocode.calendar.utils.JalaliWeekCalculator
import java.time.LocalDate
import utils.Strings

/**
 * This class provides methods to convert Gregorian dates to Jalali (Persian) dates and vice versa.
 * Acts as a facade that delegates to specialized converter classes for better maintainability.
 */
class CalendarConverter {
    companion object {

        /**
         * Returns the week number for a given Jalali date.
         *
         * @param jalaliDate The JalaliDate object
         * @return An integer representing the week number
         */
        fun getJalaliWeekNumber(jalaliDate: JalaliDate): Int {
            return JalaliWeekCalculator.getJalaliWeekNumber(jalaliDate)
        }


        /**
         * Converts a Gregorian date to a Jalali date.
         *
         * @param date The Gregorian LocalDate object
         * @return A JalaliDate object representing the Jalali date
         */
        fun gregorianToJalali(date: LocalDate): JalaliDate {
            return GregorianToJalaliConverter.gregorianToJalali(date)
        }


        /**
         * Converts a Jalali date to a Gregorian date.
         *
         * @param jalaliY The Jalali year
         * @param jalaliM The Jalali month
         * @param jalaliD The Jalali day
         * @return A LocalDate object representing the Gregorian date
         */
        fun jalaliToGregorian(jalaliY: Int, jalaliM: Int, jalaliD: Int): LocalDate {
            return JalaliToGregorianConverter.jalaliToGregorian(jalaliY, jalaliM, jalaliD)
        }


        /**
         * Returns the name of the Jalali month for a given Gregorian date.
         *
         * @param gregorianDate The Gregorian date for which to find the corresponding Jalali month
         * @return A JalaliMonth object representing the Jalali month
         */
        private fun toJalaliMonth(gregorianDate: LocalDate): JalaliMonth {
            return GregorianToJalaliConverter.toJalaliMonth(gregorianDate)
        }



         /**
          * Converts a Gregorian month to its corresponding Jalali month(s).
          *
          * This function takes a Gregorian date and determines the Jalali month(s) that overlap with the given Gregorian month.
          * It returns a map with either one or two Jalali months, depending on whether the Gregorian month spans across two Jalali months.
          *
          * @param gregorianDate The Gregorian date for which to find the corresponding Jalali month(s). The day of the month is ignored.
          * @return A map containing either one or two entries:
          *         - If the Gregorian month falls entirely within one Jalali month, the map will contain:
          *           "left" -> The Jalali month corresponding to the start of the Gregorian month
          *           "right" -> The same Jalali month as "left"
          *         - If the Gregorian month spans two Jalali months, the map will contain:
          *           "left" -> The Jalali month corresponding to the start of the Gregorian month
          *           "right" -> The Jalali month corresponding to the end of the Gregorian month
          */
         fun gregorianToJalaliMonths(gregorianDate: LocalDate): Map<String, JalaliMonth> {
             val startDate = gregorianDate.withDayOfMonth(1)
             val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())

             val startJalaliMonth = toJalaliMonth(startDate)
             val endJalaliMonth = toJalaliMonth(endDate)

             return if (startJalaliMonth.monthName != endJalaliMonth.monthName) {
                 mapOf("left" to startJalaliMonth, "right" to endJalaliMonth)
             } else {
                 mapOf("left" to startJalaliMonth, "right" to startJalaliMonth)
             }
         }
    }
}
