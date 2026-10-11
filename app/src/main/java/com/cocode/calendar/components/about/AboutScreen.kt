package com.cocode.calendar.components.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.cocode.calendar.CalColors
import com.cocode.calendar.R
import com.cocode.calendar.components.date.currentLocale

/**
 * The About screen, in the order of the cocode-apps standard: name and version, what the app does,
 * privacy, links, credits and licenses, who made it. It covers the whole screen; Back or Close
 * returns to the calendar.
 */
@Composable
fun AboutScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    // The language the app's own strings use, so the website and privacy pages match it.
    val language = currentLocale().language
    val open = { link: AboutLink -> openLink(context, aboutUrl(link, context.packageName, language)) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = CalColors.background) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AboutButton(R.string.action_close, onClose)

                Text(
                    text = stringResource(R.string.about_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CalColors.active_text,
                    modifier = Modifier.semantics { heading() }
                )
                Body(stringResource(R.string.about_version, appVersionName(context)))
                AboutButton(R.string.about_check_updates) { open(AboutLink.LatestVersion) }

                SectionTitle(R.string.about_what_title)
                Body(stringResource(R.string.about_what_body))

                SectionTitle(R.string.about_privacy_title)
                Body(stringResource(R.string.about_privacy_no_data))
                Body(stringResource(R.string.about_privacy_events))
                Body(stringResource(R.string.about_privacy_no_internet))
                Body(stringResource(R.string.about_privacy_backup))
                AboutButton(R.string.about_privacy_link) { open(AboutLink.PrivacyPolicy) }

                SectionTitle(R.string.about_links_title)
                AboutButton(R.string.about_website) { open(AboutLink.Website) }
                AboutButton(R.string.about_source) { open(AboutLink.Source) }
                AboutButton(R.string.about_report) { open(AboutLink.Issues) }

                SectionTitle(R.string.about_credits)
                Body(stringResource(R.string.about_credits_license))
                Body(stringResource(R.string.about_credits_libraries))

                SectionTitle(R.string.about_made_by)
                Body(stringResource(R.string.about_made_by_body))
                AboutButton(R.string.about_cocode) { open(AboutLink.Cocode) }

                // Support: reserved for the Support phase (cocode-apps standard/support.md).
                // Nothing is shown here until then.
            }
        }
    }
}

/** The button on the calendar screen that opens [AboutScreen]. */
@Composable
fun AboutEntryButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background),
        modifier = modifier
    ) {
        Text(text = stringResource(R.string.action_about), color = CalColors.text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(@StringRes title: Int) {
    Text(
        text = stringResource(title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = CalColors.weekday_text,
        modifier = Modifier
            .padding(top = 12.dp)
            .semantics { heading() }
    )
}

@Composable
private fun Body(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge, color = CalColors.text)
}

@Composable
private fun AboutButton(@StringRes label: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = CalColors.button_background),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(label), color = CalColors.text, fontWeight = FontWeight.Bold)
    }
}

/** The version shown to the user, read from the installed package (so it always matches the APK). */
@Suppress("DEPRECATION")
private fun appVersionName(context: Context): String =
    context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()

/** Opens [url] in the browser. Says so on screen when no app can open it. */
private fun openLink(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, R.string.about_no_browser, Toast.LENGTH_LONG).show()
    }
}
