package com.cocode.calendar.components.footer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cocode.calendar.CalColors
import com.cocode.calendar.R

/**
 * The footer of the calendar screen: one link that opens the About screen, where the app's
 * maker, licence and links are.
 *
 * @param onAboutClick Opens the About screen.
 * @param modifier Modifier to be applied to the footer container
 */
@Composable
fun FooterInfo(onAboutClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.action_about),
            style = MaterialTheme.typography.bodyLarge,
            color = CalColors.active_text,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CalColors.prev_month_background.copy(alpha = 0.8f))
                .clickable(role = Role.Button, onClick = onAboutClick)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}
