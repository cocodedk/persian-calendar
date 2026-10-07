package com.cocode.calendar.components.about

/** The pages the About screen can open. */
enum class AboutLink { LatestVersion, PrivacyPolicy, Website, Source, Issues, Cocode }

/**
 * Set this to true once F-Droid lists the app (`fdroid: live` in cocode-apps' apps.yml).
 * Until then "See the latest version" opens the newest GitHub release. The app never checks for
 * updates over the network: the button only opens a page in the browser.
 */
const val LIVE_ON_FDROID = false

private const val REPOSITORY = "https://github.com/cocodedk/persian-calendar"
private const val SITE = "https://calendar.cocode.dk"

/**
 * Languages the site has both a home page and a privacy page for, at `<site>/<code>/` and
 * `<site>/<code>/privacy/`. Any other language opens the English pages.
 */
private val SITE_LANGUAGES = setOf("da")

private fun sitePage(language: String, path: String = ""): String = when {
    language in SITE_LANGUAGES -> "$SITE/$language/$path"
    path.isEmpty() -> SITE
    else -> "$SITE/$path"
}

/**
 * The address each About link opens. The website and privacy links follow [language] and open the
 * English pages when the site has none in that language.
 *
 * @param applicationId the app's package name, used for the F-Droid page
 * @param language the app's current language code, such as "da" (from its current locale)
 * @param liveOnFdroid whether the app is listed on F-Droid
 */
fun aboutUrl(
    link: AboutLink,
    applicationId: String,
    language: String,
    liveOnFdroid: Boolean = LIVE_ON_FDROID
): String = when (link) {
    AboutLink.LatestVersion ->
        if (liveOnFdroid) "https://f-droid.org/packages/$applicationId/" else "$REPOSITORY/releases/latest"
    AboutLink.PrivacyPolicy -> sitePage(language, "privacy/")
    AboutLink.Website -> sitePage(language)
    AboutLink.Source -> REPOSITORY
    AboutLink.Issues -> "$REPOSITORY/issues"
    AboutLink.Cocode -> "https://cocode.dk"
}
