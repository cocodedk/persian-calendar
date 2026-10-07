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
            aboutUrl(AboutLink.LatestVersion, applicationId, "en", liveOnFdroid = false)
        )
    }

    @Test
    fun `should open the F-Droid page once the app is on F-Droid`() {
        assertEquals(
            "https://f-droid.org/packages/com.cocode.calendar/",
            aboutUrl(AboutLink.LatestVersion, applicationId, "en", liveOnFdroid = true)
        )
    }

    @Test
    fun `should not claim to be on F-Droid until the flag is switched on`() {
        assertFalse(LIVE_ON_FDROID)
    }

    @Test
    fun `should open the English website and privacy policy in English`() {
        assertEquals("https://calendar.cocode.dk", aboutUrl(AboutLink.Website, applicationId, "en"))
        assertEquals("https://calendar.cocode.dk/privacy/", aboutUrl(AboutLink.PrivacyPolicy, applicationId, "en"))
    }

    @Test
    fun `should open the Danish website and privacy policy in Danish`() {
        assertEquals("https://calendar.cocode.dk/da/", aboutUrl(AboutLink.Website, applicationId, "da"))
        assertEquals(
            "https://calendar.cocode.dk/da/privacy/",
            aboutUrl(AboutLink.PrivacyPolicy, applicationId, "da")
        )
    }

    @Test
    fun `should open the English pages in a language the site lacks`() {
        listOf("fa", "de").forEach { language ->
            assertEquals("https://calendar.cocode.dk", aboutUrl(AboutLink.Website, applicationId, language))
            assertEquals(
                "https://calendar.cocode.dk/privacy/",
                aboutUrl(AboutLink.PrivacyPolicy, applicationId, language)
            )
        }
    }

    @Test
    fun `should link the source code and issue tracker in every language`() {
        listOf("en", "da", "fa").forEach { language ->
            assertEquals(
                "https://github.com/cocodedk/persian-calendar",
                aboutUrl(AboutLink.Source, applicationId, language)
            )
            assertEquals(
                "https://github.com/cocodedk/persian-calendar/issues",
                aboutUrl(AboutLink.Issues, applicationId, language)
            )
        }
    }

    @Test
    fun `should link cocode dot dk`() {
        assertEquals("https://cocode.dk", aboutUrl(AboutLink.Cocode, applicationId, "en"))
    }

    @Test
    fun `should only open secure addresses`() {
        AboutLink.values().forEach { link ->
            listOf("en", "da", "fa").forEach { language ->
                assertTrue(link.name, aboutUrl(link, applicationId, language).startsWith("https://"))
            }
        }
    }
}
