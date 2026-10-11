package com.cocode.calendar.converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.Event
import com.cocode.calendar.EventDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** An event store that holds nothing: the converter never reads it. */
private class NoEvents : EventDao {
    override fun getAllEvents(): Flow<List<Event>> = flowOf(emptyList())
    override fun getEventsForDate(date: String): Flow<List<Event>> = flowOf(emptyList())
    override suspend fun insertEvent(event: Event) = Unit
    override suspend fun updateEvent(event: Event) = Unit
    override suspend fun deleteEvent(event: Event) = Unit
}

/** A [CalendarViewModel] with the converter open, in Jalali to Gregorian mode like the app starts. */
fun openConverterViewModel(): CalendarViewModel =
    CalendarViewModel(NoEvents()).also { it.toggleConverter() }

/** Shows [content] with [viewModel] found by `viewModel()`, at text size [fontScale]. */
fun ComposeContentTestRule.setConverterContent(
    viewModel: CalendarViewModel,
    fontScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val owner = object : ViewModelStoreOwner {
        override val viewModelStore = ViewModelStore()
    }
    ViewModelProvider(owner, object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModel as T
    })[CalendarViewModel::class.java]
    setContent {
        val density = LocalDensity.current
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides owner,
            LocalDensity provides Density(density.density, fontScale)
        ) { content() }
    }
}

/** The Year, Month or Day input of the converter, found by its label. */
fun ComposeContentTestRule.field(label: String): SemanticsNodeInteraction =
    onNode(hasSetTextAction() and hasText(label))

/** Replaces the three fields with [year], [month] and [day]. */
fun ComposeContentTestRule.enterDate(year: String, month: String, day: String) {
    field("Year").performTextReplacement(year)
    field("Month").performTextReplacement(month)
    field("Day").performTextReplacement(day)
}

fun ComposeContentTestRule.swapButton() = onNodeWithContentDescription("Convert from", substring = true)

fun ComposeContentTestRule.closeButton() = onNodeWithContentDescription("Close converter")

fun ComposeContentTestRule.title() = onNodeWithText("Date Converter", substring = true)

/** Where the node is on screen, not cut off by a parent that clips or scrolls it. */
fun SemanticsNodeInteraction.bounds(): Rect {
    val node: SemanticsNode = fetchSemanticsNode()
    return Rect(node.positionInRoot, Size(node.size.width.toFloat(), node.size.height.toFloat()))
}
