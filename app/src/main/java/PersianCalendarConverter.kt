import android.os.Build
import androidx.annotation.RequiresApi
import java.time.YearMonth

class PersianCalendarConverter {
    companion object {
        private val gregorianMonthDays = arrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        private val persianMonthNames = arrayOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر",
            "مرداد", "شهریور", "مهر", "آبان",
            "آذر", "دی", "بهمن", "اسفند"
        )

        fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
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

            val month = if (days < 186) 1 + days / 31 else 7 + (days - 186) / 30
            val day = 1 + if (days < 186) days % 31 else (days - 186) % 30

            return Triple(year, month, day)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getJalaliMonthName(yearMonth: YearMonth): Triple<String, Int, Int> {

            // First convert the YearMonth to a LocalDate representing the first of the month
            val firstDayOfMonth = yearMonth.atDay(1)

            // Convert the LocalDate to the Jalali date
            val (year, month, day) = gregorianToJalali(
                firstDayOfMonth.year,
                firstDayOfMonth.monthValue,
                firstDayOfMonth.dayOfMonth
            )

            // 'month' is the Jalali month number. Use it to get the month name from the array.
            // Adjust for zero-based index
            return Triple(persianMonthNames[month - 1], year, month)
        }
    }
}