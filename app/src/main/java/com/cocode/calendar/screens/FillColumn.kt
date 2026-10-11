package com.cocode.calendar.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

/**
 * A column of three parts, top to bottom, in which [fill] takes the height that [top] and [bottom]
 * leave of [viewportHeight], but never less than [fillMinHeight]. Inside a vertical scroll this
 * lets the calendar grid fill a tall screen and still scroll when large text needs more room.
 * Each part is centred horizontally.
 */
@Composable
fun FillColumn(
    viewportHeight: Dp,
    fillMinHeight: Dp,
    modifier: Modifier = Modifier,
    top: @Composable () -> Unit,
    fill: @Composable () -> Unit,
    bottom: @Composable () -> Unit
) {
    Layout(contents = listOf(top, fill, bottom), modifier = modifier) { (tops, fills, bottoms), constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0, maxHeight = Constraints.Infinity)
        val topPlaced = tops.map { it.measure(loose) }
        val bottomPlaced = bottoms.map { it.measure(loose) }
        val used = topPlaced.sumOf { it.height } + bottomPlaced.sumOf { it.height }
        val fillHeight = maxOf(fillMinHeight.roundToPx(), viewportHeight.roundToPx() - used)
        val fillPlaced = fills.map { it.measure(loose.copy(minHeight = fillHeight, maxHeight = fillHeight)) }
        val width = constraints.maxWidth
        layout(width, used + fillHeight * fillPlaced.size) {
            var y = 0
            (topPlaced + fillPlaced + bottomPlaced).forEach {
                it.placeRelative((width - it.width) / 2, y)
                y += it.height
            }
        }
    }
}
