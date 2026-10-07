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

/**
 * The address each About link opens.
 *
 * @param applicationId the app's package name, used for the F-Droid page
 * @param liveOnFdroid whether the app is listed on F-Droid
 */
fun aboutUrl(
    link: AboutLink,
    applicationId: String,
    liveOnFdroid: Boolean = LIVE_ON_FDROID
): String = when (link) {
    AboutLink.LatestVersion ->
        if (liveOnFdroid) "https://f-droid.org/packages/$applicationId/" else "$REPOSITORY/releases/latest"
    AboutLink.PrivacyPolicy -> "https://calendar.cocode.dk/privacy/"
    AboutLink.Website -> "https://calendar.cocode.dk"
    AboutLink.Source -> REPOSITORY
    AboutLink.Issues -> "$REPOSITORY/issues"
    AboutLink.Cocode -> "https://cocode.dk"
}
