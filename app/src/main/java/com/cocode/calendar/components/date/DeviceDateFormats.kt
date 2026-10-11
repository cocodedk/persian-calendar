package com.cocode.calendar.components.date

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import utils.DateFormats

/** The phone's own best pattern for a skeleton, for [DateFormats]. */
val deviceBestPattern = DateFormats.BestPattern { locale, skeleton ->
    DateFormat.getBestDateTimePattern(locale, skeleton)
}

/** The language the app is shown in (follows a per-app language if the phone has one). */
@Composable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

/** Whether the phone is set to a 24-hour clock. */
@Composable
fun phoneUses24HourClock(): Boolean {
    val context = LocalContext.current
    // Read together with the locale, so the value is read again when the configuration changes
    LocalConfiguration.current
    return DateFormat.is24HourFormat(context)
}
