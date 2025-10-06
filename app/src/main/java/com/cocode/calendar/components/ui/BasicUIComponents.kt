package com.cocode.calendar.components.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.calendar.CalColors

/**
 * Creates a spacer cell in a Compose layout.
 *
 * This composable function generates an empty Box that acts as a spacer
 * in the layout. It's useful for creating gaps or empty spaces between
 * other composable.
 *
 * @param width The width of the spacer as a fraction of its parent's width.
 *              Defaults to 0.5f (50% of the parent's width).
 * @param height The height of the spacer. Defaults to 50.dp.
 * @return A composable [Box] that serves as a spacer in the layout.
 */
@Composable
fun SpacerCell(
    width: Float = 0.5f,
    height: androidx.compose.ui.unit.Dp = 50.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(width)
            .height(height)
    )
}

/**
 * Creates a centered text element within a Box composable.
 *
 * This function creates a Box with a fixed height and places a Text composable
 * inside it. The text is centered both horizontally and vertically by default.
 *
 * @param text The string to be displayed in the Text composable.
 * @param fontSize The size of the font for the text. Defaults to 12.sp.
 * @param fontWeight The weight of the font. Defaults to FontWeight.Bold.
 * @param color The color of the text. Defaults to CalColors.text.
 * @param textAlign The text alignment. Defaults to TextAlign.Center.
 * @param contentAlignment The alignment of the content within the Box.
 *        Defaults to Alignment.Center.
 * @param modifier Additional modifier to be applied to the Box.
 * @param width The width of the text element. Defaults to 20.dp.
 *
 * @return A composable that displays centered text within a Box.
 */
@Composable
fun CenteredText(
    text: String,
    fontSize: TextUnit = 12.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = CalColors.text,
    textAlign: TextAlign = TextAlign.Center,
    contentAlignment: Alignment = Alignment.Center,
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = 20.dp
) {
    Box(
        modifier = modifier.height(50.dp),
        contentAlignment = contentAlignment
    ) {
        Text(
            text = text,
            modifier = Modifier.width(width),
            textAlign = textAlign,
            fontWeight = fontWeight,
            color = color,
            fontSize = fontSize
        )
    }
}
