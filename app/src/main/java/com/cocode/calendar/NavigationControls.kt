package com.cocode.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.components.interactive.CrossClickArea

/**
 * NavigationControls.kt - Facade for navigation control components.
 *
 * This file now acts as a simple facade that delegates to the specialized
 * UI and interactive components for better maintainability and organization.
 */

// Re-export the CrossClickArea component for backward compatibility
@Composable
fun CrossClickArea(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    onClickUp: () -> Unit,
    onClickDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    com.cocode.calendar.components.interactive.CrossClickArea(
        onClickLeft = onClickLeft,
        onClickRight = onClickRight,
        onClickUp = onClickUp,
        onClickDown = onClickDown,
        modifier = modifier
    )
}
