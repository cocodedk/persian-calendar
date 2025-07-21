import java.time.LocalDate
import utils.Strings

/**
 * This class provides methods to convert Gregorian dates to Jalali (Persian) dates and vice versa.
 */
class CalendarConverter {
    companion object {
        private val gregorianMonthDays = arrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

        private val gregorianDaysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        private val jalaliDaysInMonth = arrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        /**
         * Data class to hold the Jalali month name and the Jalali date.
         */

        data class JalaliDate(val year: Int, val monthValue: Int, val dayOfMonth: Int)
        data class JalaliMonth(val monthName: String, val monthValue: Int, val year: Int)

        /**
         * Returns the week number for a given Jalali date.
         *
         * @param jalaliDate The JalaliDate object
         * @return An integer representing the week number
         */
        fun getJalaliWeekNumber(jalaliDate: JalaliDate): Int {
            // Calculate the number of days passed since the start of the Jalali year
            val daysPassed = jalaliDaysInMonth.take(jalaliDate.monthValue - 1).sum() + jalaliDate.dayOfMonth

            // Calculate the week number
            val weekNumber = (daysPassed + 6) / 7

            return weekNumber
        }


        /**
         * Converts a Gregorian date to a Jalali date.
         *
         * @param date The Gregorian LocalDate object
         * @return A JalaliDate object representing the Jalali date
         */
        fun gregorianToJalali(date: LocalDate): JalaliDate {
            val gregorianYear = date.year
            val gregorianMonth = date.monthValue
            val gregorianDay = date.dayOfMonth

            val (jalaliYear, adjustedGregorianYear) = if (gregorianYear > 1600) {
                979 to gregorianYear - 1600
            } else {
                0 to gregorianYear - 621
            }

            val adjustedGregorianYear2 = adjustedGregorianYear + if (gregorianMonth > 2) 1 else 0
            var days = (365 * adjustedGregorianYear) +
                       ((adjustedGregorianYear2 + 3) / 4) -
                       ((adjustedGregorianYear2 + 99) / 100) +
                       ((adjustedGregorianYear2 + 399) / 400) -
                       80 + gregorianDay + gregorianMonthDays[gregorianMonth - 1]

            var finalJalaliYear = jalaliYear + 33 * (days / 12053)
            days %= 12053
            finalJalaliYear += 4 * (days / 1461)
            days %= 1461

            if (days > 365) {
                finalJalaliYear += (days - 1) / 365
                days = (days - 1) % 365
            }

            val monthValue = if (days < 186) 1 + days / 31 else 7 + (days - 186) / 30
            val dayOfMonth = 1 + if (days < 186) days % 31 else (days - 186) % 30

            return JalaliDate(finalJalaliYear, monthValue, dayOfMonth)
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

            val jalaliYear = jalaliY - 979
            val jalaliMonth = jalaliM - 1
            val jalaliDay = jalaliD - 1

            var jalaliDayNo = 365 * jalaliYear + (jalaliYear / 33) * 8 + ((jalaliYear % 33 + 3) / 4)
            for (i in 0 until jalaliMonth) jalaliDayNo += jalaliDaysInMonth[i]

            jalaliDayNo += jalaliDay

            var gregorianDayNo = jalaliDayNo + 79

            var gregorianYear = 1600 + 400 * (gregorianDayNo / 146097) /* 146097 = 365*400 + 400/4 - 400/100 + 400/400 */
            gregorianDayNo %= 146097

            var leap = true
            if (gregorianDayNo >= 36525) /* 36525 = 365*100 + 100/4 */ {
                gregorianDayNo--
                gregorianYear += 100 * (gregorianDayNo / 36524) /* 36524 = 365*100 + 100/4 - 100/100 */
                gregorianDayNo %= 36524

                if (gregorianDayNo >= 365) gregorianDayNo++
                else leap = false
            }

            gregorianYear += 4 * (gregorianDayNo / 1461) /* 1461 = 365*4 + 4/4 */
            gregorianDayNo %= 1461

            if (gregorianDayNo >= 366) {
                leap = false

                gregorianDayNo--
                gregorianYear += gregorianDayNo / 365
                gregorianDayNo %= 365
            }

            var i = 0
            while (gregorianDayNo >= gregorianDaysInMonth[i] + if (i == 1 && leap) 1 else 0) {
                gregorianDayNo -= gregorianDaysInMonth[i] + if (i == 1 && leap) 1 else 0
                i++
            }
            val gregorianMonth = i + 1
            val gregorianDay = gregorianDayNo + 1

            return LocalDate.of(gregorianYear, gregorianMonth, gregorianDay)
        }


        /**
         * Returns the name of the Jalali month for a given YearMonth object.
         *
         * @return A JalaliMonth object representing the Jalali month
         */
        private fun toJalaliMonth(gregorianDate: LocalDate): JalaliMonth {
            val jalaliDate = gregorianToJalali(gregorianDate)
            // 'month' is the Jalali month number. Use it to get the month name from the centralized Strings object.
            // Adjust for zero-based index
            val monthName = Strings.Months.JALALI_PERSIAN[jalaliDate.monthValue - 1]

            return JalaliMonth(monthName, jalaliDate.monthValue, jalaliDate.year)
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
