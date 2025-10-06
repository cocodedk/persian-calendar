package com.cocode.calendar.components.interactive

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.components.ui.CenteredText
import com.cocode.calendar.components.ui.SpacerCell
import kotlinx.coroutines.delay
import utils.Strings

/**
 * Creates a composable cross-shaped click area with directional controls.
 *
 * This function generates a layout with clickable areas for up, down, left, and right directions,
 * along with year and month indicators. It's designed to provide intuitive navigation controls
 * for a calendar or similar date-based interface.
 *
 * @param onClickLeft Lambda to be invoked when the left arrow is clicked or long-pressed.
 * @param onClickRight Lambda to be invoked when the right arrow is clicked or long-pressed.
 * @param onClickUp Lambda to be invoked when the up arrow is clicked or long-pressed.
 * @param onClickDown Lambda to be invoked when the down arrow is clicked or long-pressed.
 * @param modifier Modifier to be applied to the main column layout of this composable.
 *
 * @return A composable that displays a cross-shaped click area with directional controls.
 */
@Composable
fun CrossClickArea(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    onClickUp: () -> Unit,
    onClickDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()

    if (showConverter) {
        return
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            SpacerCell(width = 0.2f)
            ClickableCell(
                onClick = onClickUp, onLongPress = onClickUp, width = 0.75f,
                icon = Icons.Default.KeyboardArrowUp, contentDescription = Strings.Calendar.NEXT_YEAR)
            SpacerCell(width = 1f)
        }

        Text(Strings.Calendar.Controls.NEXT_YEAR, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold, color = CalColors.text, fontSize = 12.sp)

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()

        ) {
            ClickableCell(
                onClick = onClickLeft,
                onLongPress = onClickLeft,
                width = 0.2f,
                icon = Icons.Default.KeyboardArrowLeft,
                contentDescription = Strings.Calendar.PREVIOUS_MONTH
            )
            CenteredText(Strings.Calendar.Controls.PREVIOUS_MONTH)
            SpacerCell(width = 0.67f)  // Optionally, this cell can be interactive or display info.
            CenteredText(Strings.Calendar.Controls.NEXT_MONTH)
            ClickableCell(
                onClick = onClickRight, onLongPress = onClickRight, width = 1f,
                icon = Icons.Default.KeyboardArrowRight, contentDescription = Strings.Calendar.NEXT_MONTH
            )

        }

        Text(Strings.Calendar.Controls.PREVIOUS_YEAR, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold, color = CalColors.text, fontSize = 12.sp)

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            SpacerCell(0.2f)
            ClickableCell(onClick = onClickDown, onLongPress = onClickDown, width = 0.75f,
                icon = Icons.Default.KeyboardArrowDown, contentDescription = Strings.Calendar.NEXT_YEAR)
            SpacerCell(1f)
        }
    }
}

/**
 * Creates a clickable cell with an icon that responds to both click and long-press events.
 *
 * This composable function creates an interactive cell with an icon that can be clicked or long-pressed.
 * It supports continuous action on long-press, repeating the action at regular intervals.
 *
 * @param onClick A lambda function that is called when the cell is clicked.
 * @param onLongPress A lambda function that is called repeatedly while the cell is being long-pressed.
 * @param width The width of the cell as a fraction of its parent's width. Defaults to 0.5f.
 * @param icon The [ImageVector] to be displayed in the cell.
 * @param contentDescription An optional string describing the icon for accessibility purposes.
 *
 * @OptIn(ExperimentalComposeUiApi::class) This function uses experimental Compose UI APIs.
 * @Composable This function is a Jetpack Compose composable.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClickableCell(
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    width: Float = 0.5f,
    icon: ImageVector,
    contentDescription: String? = null
) {
    var holding by remember { mutableStateOf(false) }
    LaunchedEffect(holding) {
        while (holding) {
            onLongPress()
            delay(300) // Delay between repeated actions, adjust as necessary
        }
    }
    Icon(
        icon,
        contentDescription = contentDescription,
        tint = CalColors.text,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(width)
            .pointerInteropFilter {
                when (it.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        holding = true
                        onClick() // Also trigger onClick at the start
                    }

                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        holding = false
                    }
                }
                true
            }
    )
}
