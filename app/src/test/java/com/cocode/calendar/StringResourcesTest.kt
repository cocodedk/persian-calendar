package com.cocode.calendar

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.w3c.dom.Element

/**
 * Checks the month and day names in res/values/strings.xml. They are plain data in the app, and a
 * slip in one of them (a missing month, a changed letter) would show on the calendar header.
 */
class StringResourcesTest {

    private fun array(name: String): List<String> {
        val file = File("src/main/res/values/strings.xml")
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val arrays = document.getElementsByTagName("string-array")
        for (i in 0 until arrays.length) {
            val element = arrays.item(i) as Element
            if (element.getAttribute("name") == name) {
                val items = element.getElementsByTagName("item")
                return (0 until items.length).map { items.item(it).textContent }
            }
        }
        error("No string-array named $name")
    }

    @Test
    fun `should list the Persian month names in Persian script, Farvardin first`() {
        assertEquals(
            listOf(
                "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
                "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
            ),
            array("jalali_months_persian")
        )
    }

    @Test
    fun `should list the short Gregorian month names, January first`() {
        assertEquals(
            listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
            array("gregorian_months_short")
        )
    }

    @Test
    fun `should list the week days with Sunday first`() {
        assertEquals(listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"), array("weekday_short"))
    }
}
