package com.cocode.calendar

import androidx.room.TypeConverter
import java.time.LocalDate

object EventConverters {
    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    @JvmStatic
    fun toLocalDate(dateString: String?): LocalDate? = dateString?.let { LocalDate.parse(it) }
}
