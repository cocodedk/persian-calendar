import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.YearMonth

/**
 * This class provides methods to convert Gregorian dates to Jalali (Persian) dates and vice versa.
 */
class CalendarConverter {
    companion object {
        private val gregorianMonthDays = arrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        private val persianMonthNames = arrayOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر",
            "مرداد", "شهریور", "مهر", "آبان",
            "آذر", "دی", "بهمن", "اسفند"
        )

        private val gregorianDaysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        private val jalaliDaysInMonth = arrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        /**
         * Data class to hold the Jalali month name and the Jalali date.
         */

        data class JalaliDate(val year: Int, val monthValue: Int, val dayOfMonth: Int)
        data class JalaliMonth(val monthName: String, val monthValue: Int, val year: Int)

        /**
         * Converts a Gregorian date to a Jalali date.
         *
         * @param date The Gregorian LocalDate object
         * @return A JalaliDate object representing the Jalali date
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun gregorianToJalali(date: LocalDate): JalaliDate {
            val gy = date.year
            val gm = date.monthValue
            val gd = date.dayOfMonth
            var year: Int
            val gyAdjusted = if (gy > 1600) {
                year = 979
                gy - 1600
            } else {
                year = 0
                gy - 621
            }

            val gy2 = if (gm > 2) gyAdjusted + 1 else gyAdjusted
            var days: Int = (365 * gyAdjusted) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) - 80 + gd + gregorianMonthDays[gm - 1]
            year += 33 * (days / 12053)
            days %= 12053
            year += 4 * (days / 1461)
            days %= 1461

            if (days > 365) {
                year += (days - 1) / 365
                days = (days - 1) % 365
            }

            val monthValue = if (days < 186) 1 + days / 31 else 7 + (days - 186) / 30
            val dayOfMonth = 1 + if (days < 186) days % 31 else (days - 186) % 30

            return JalaliDate(year, monthValue, dayOfMonth)
        }

        /**
         * Converts a Jalali date to a Gregorian date.
         *
         * @param jalaliY The Jalali year
         * @param jalaliM The Jalali month
         * @param jalaliD The Jalali day
         * @return A LocalDate object representing the Gregorian date
         */
        @RequiresApi(Build.VERSION_CODES.O)
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
         * @param yearMonth The YearMonth object in Gregorian calendar
         * @return A JalaliMonth object representing the Jalali month
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun toJalaliMonth(gregorianDate: LocalDate): JalaliMonth {

            // First convert the YearMonth to a LocalDate representing the first of the month
            val firstDayOfMonth = YearMonth.from(gregorianDate).atDay(1)

            // Convert the LocalDate to the Jalali date
            val jalaliDate = gregorianToJalali(firstDayOfMonth)

            // 'month' is the Jalali month number. Use it to get the month name from the array.
            // Adjust for zero-based index
            val monthName = persianMonthNames[jalaliDate.monthValue - 1]

            return JalaliMonth(monthName, jalaliDate.monthValue, jalaliDate.year)
        }
    }
}