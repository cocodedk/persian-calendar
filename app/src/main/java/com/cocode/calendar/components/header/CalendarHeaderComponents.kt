package com.cocode.calendar.components.header

import CalendarConverter
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.R
import com.cocode.calendar.models.JalaliMonth
import utils.DateTimeUtils
import java.time.LocalDate
import com.cocode.calendar.components.date.currentLocale
import com.cocode.calendar.components.date.deviceBestPattern
import utils.DateFormats

/**
 * Calendar-specific header component that displays calendar information.
 * This component is focused specifically on calendar display without time information.
 */
@Composable
fun CalendarHeader() {
    val viewModel: CalendarViewModel = viewModel()
    val gregorianDate by viewModel.gregorianDate.observeAsState(initial = LocalDate.now())
    val isJalaliCalendar by viewModel.isJalaliCalendar.observeAsState(initial = false)

    val resources = LocalResources.current
    val locale = currentLocale()
    val persianMonths = stringArrayResource(R.array.jalali_months_persian)
    val (primaryText, secondaryText) = remember(gregorianDate, isJalaliCalendar, resources, persianMonths, locale) {
        val jalaliMonths = CalendarConverter.gregorianToJalaliMonths(gregorianDate)
        val jalaliDate = CalendarConverter.gregorianToJalali(gregorianDate)
        val jalaliWeekNumber = CalendarConverter.getJalaliWeekNumber(jalaliDate)
        fun weekLabel(week: Int) = resources.getString(R.string.header_week, week.toString())
        fun monthName(month: JalaliMonth?) = month?.let { persianMonths[it.monthValue - 1] }

        val right = jalaliMonths["right"]
        val jalaliText = "${weekLabel(jalaliWeekNumber)} - ${monthName(jalaliMonths["left"])} - ${monthName(right)} ${right?.year}"
        val gregorianText = "${DateFormats.monthYear(gregorianDate, locale, deviceBestPattern)} - ${weekLabel(DateTimeUtils.getCurrentWeekNumber(gregorianDate))}"

        if (isJalaliCalendar) jalaliText to gregorianText else gregorianText to jalaliText
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = primaryText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = CalColors.active_text
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = secondaryText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = CalColors.inactive_text,
            fontSize = 12.sp
        )
    }
}
