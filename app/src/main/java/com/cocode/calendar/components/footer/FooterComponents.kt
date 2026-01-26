package com.cocode.calendar.components.footer

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.cocode.calendar.CalColors

/**
 * This Composable function displays the footer information with the developer name and company
 * in a fancy, professional style. It shows "Babak Bandpey" and "cocode.dk" with enhanced visual design.
 *
 * @param modifier Modifier to be applied to the footer container
 * @Composable This annotation indicates that this function is a Composable function in Jetpack Compose.
 */
@Composable
fun FooterInfo(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Fancy container with background and rounded corners
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CalColors.prev_month_background.copy(alpha = 0.8f))
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            val annotatedString = buildAnnotatedString {
                // Developer name with enhanced styling
                pushStringAnnotation(tag = "URL", annotation = "https://www.linkedin.com/in/babakbandpey/")
                withStyle(
                    style = SpanStyle(
                        color = CalColors.active_text,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("Babak Bandpey")
                }
                pop()

                // Elegant separator
                withStyle(
                    style = SpanStyle(
                        color = CalColors.inactive_text,
                        fontWeight = FontWeight.Light
                    )
                ) {
                    append(" • ")
                }

                // Company name with enhanced styling
                pushStringAnnotation(tag = "URL", annotation = "https://cocode.dk")
                withStyle(
                    style = SpanStyle(
                        color = CalColors.active_text,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("cocode.dk")
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                ),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            val intent = Intent(Intent.ACTION_VIEW, annotation.item.toUri())
                            context.startActivity(intent)
                        }
                }
            )
        }
    }
}
