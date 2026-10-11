package com.cocode.calendar.components.about

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AboutLinksTest {

    private val applicationId = "com.cocode.calendar"

    @Test
    fun `should open the latest GitHub release while the app is not on F-Droid`() {
        assertEquals(
            "https://github.com/cocodedk/persian-calendar/releases/latest",
            aboutUrl(AboutLink.LatestVersion, applicationId, liveOnFdroid = false)
        )
    }

    @Test
    fun `should open the F-Droid page once the app is on F-Droid`() {
        assertEquals(
            "https://f-droid.org/packages/com.cocode.calendar/",
            aboutUrl(AboutLink.LatestVersion, applicationId, liveOnFdroid = true)
        )
    }

    @Test
    fun `should not claim to be on F-Droid until the flag is switched on`() {
        assertFalse(LIVE_ON_FDROID)
    }

    @Test
    fun `should link the privacy policy on the app site`() {
        assertEquals("https://calendar.cocode.dk/privacy/", aboutUrl(AboutLink.PrivacyPolicy, applicationId))
    }

    @Test
    fun `should link the website, source code and issue tracker`() {
        assertEquals("https://calendar.cocode.dk", aboutUrl(AboutLink.Website, applicationId))
        assertEquals("https://github.com/cocodedk/persian-calendar", aboutUrl(AboutLink.Source, applicationId))
        assertEquals("https://github.com/cocodedk/persian-calendar/issues", aboutUrl(AboutLink.Issues, applicationId))
    }

    @Test
    fun `should link cocode dot dk`() {
        assertEquals("https://cocode.dk", aboutUrl(AboutLink.Cocode, applicationId))
    }

    @Test
    fun `should only open secure addresses`() {
        AboutLink.values().forEach { link ->
            assertTrue(link.name, aboutUrl(link, applicationId).startsWith("https://"))
        }
    }
}
