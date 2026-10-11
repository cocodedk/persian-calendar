package com.cocode.calendar.converter

import com.cocode.calendar.models.CalendarConstants
import com.cocode.calendar.models.JalaliDate
import java.time.LocalDate

/**
 * Converter for transforming Jalali dates to Gregorian dates.
 */
object JalaliToGregorianConverter {

    /**
     * Converts a Jalali date to a Gregorian date.
     *
     * @param jalaliY The Jalali year
     * @param jalaliM The Jalali month
     * @param jalaliD The Jalali day
     * @return A LocalDate object representing the Gregorian date
     * @throws IllegalArgumentException when the Jalali date does not exist, such as Esfand 30 in a common year
     */
    fun jalaliToGregorian(jalaliY: Int, jalaliM: Int, jalaliD: Int): LocalDate {
        require(jalaliM in 1..12) { "No Jalali month $jalaliM" }
        val jalaliYear = jalaliY - 979
        val jalaliMonth = jalaliM - 1
        val jalaliDay = jalaliD - 1

        var jalaliDayNo = 365 * jalaliYear + (jalaliYear / 33) * 8 + ((jalaliYear % 33 + 3) / 4)
        for (i in 0 until jalaliMonth) jalaliDayNo += CalendarConstants.jalaliDaysInMonth[i]

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
        while (gregorianDayNo >= CalendarConstants.gregorianDaysInMonth[i] + if (i == 1 && leap) 1 else 0) {
            gregorianDayNo -= CalendarConstants.gregorianDaysInMonth[i] + if (i == 1 && leap) 1 else 0
            i++
        }
        val gregorianMonth = i + 1
        val gregorianDay = gregorianDayNo + 1

        val gregorian = LocalDate.of(gregorianYear, gregorianMonth, gregorianDay)
        // The arithmetic above rolls an impossible day over into the next month; the way back exposes it.
        require(GregorianToJalaliConverter.gregorianToJalali(gregorian) == JalaliDate(jalaliY, jalaliM, jalaliD)) {
            "No such Jalali date: $jalaliY/$jalaliM/$jalaliD"
        }
        return gregorian
    }
}
