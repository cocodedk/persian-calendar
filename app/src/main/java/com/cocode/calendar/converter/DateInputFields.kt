package com.cocode.calendar.converter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.cocode.calendar.R

/** The width each field gets side by side: Year 1.5, Month 1, Day 1. */
private val FIELD_WEIGHTS = listOf(1.5f, 1f, 1f)
private val FIELD_GAP = 12.dp

/** A label sits 16.dp from each edge of its field; 1.dp more keeps it clear of rounding. */
private val LABEL_PADDING = 17.dp

/**
 * True when every label fits on one line in its field with the three fields side by side.
 * [labelWidths] are in pixels, in the order Year, Month, Day; [rowWidth], [gap] and [labelPadding] too.
 */
fun labelsFit(labelWidths: List<Float>, rowWidth: Float, gap: Float, labelPadding: Float): Boolean {
    val unit = (rowWidth - gap * (FIELD_WEIGHTS.size - 1)) / FIELD_WEIGHTS.sum()
    return labelWidths.indices.all { labelWidths[it] + 2 * labelPadding <= unit * FIELD_WEIGHTS[it] }
}

private class DateFieldSpec(
    val label: String,
    val value: String,
    val onValueChange: (String) -> Unit,
    val onNext: () -> Unit
)

/**
 * Year, Month and Day side by side, or one under the other when their labels would not fit on
 * one line (a large text size, a narrow screen), so no label breaks inside a word.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DateInputFields(
    year: String,
    month: String,
    day: String,
    onYearChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    onYearDone: () -> Unit,
    onMonthDone: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val yearFocusRequester = remember { FocusRequester() }

    // Auto-focus the Year field when the component is first displayed
    LaunchedEffect(Unit) {
        yearFocusRequester.requestFocus()
    }

    val fields = listOf(
        DateFieldSpec(stringResource(R.string.converter_field_year), year, { input ->
            if (input.isEmpty() || input.length <= FieldAdvance.YEAR_DIGITS && input.all { it.isDigit() }) {
                onYearChange(input)
            }
        }, onYearDone),
        DateFieldSpec(stringResource(R.string.converter_field_month), month, { input ->
            val num = input.toIntOrNull()
            if (input.isEmpty() || input.length <= 2 && num != null && num in 1..FieldAdvance.MONTH_MAX) {
                onMonthChange(input)
            }
        }, onMonthDone),
        DateFieldSpec(stringResource(R.string.converter_field_day), day, { input ->
            val num = input.toIntOrNull()
            if (input.isEmpty() || input.length <= 2 && num != null && num in 1..FieldAdvance.DAY_MAX) {
                onDayChange(input)
            }
        }, {})
    )
    val onDone = {
        keyboardController?.hide()
        focusManager.clearFocus()
    }
    fun Modifier.yearFocus(index: Int) = if (index == 0) focusRequester(yearFocusRequester) else this

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        val measurer = rememberTextMeasurer()
        val labelStyle = MaterialTheme.typography.labelSmall
        val sideBySide = labelsFit(
            labelWidths = fields.map { measurer.measure(it.label, labelStyle).size.width.toFloat() },
            rowWidth = constraints.maxWidth.toFloat(),
            gap = with(density) { FIELD_GAP.toPx() },
            labelPadding = with(density) { LABEL_PADDING.toPx() }
        )
        if (sideBySide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FIELD_GAP)
            ) {
                fields.forEachIndexed { index, field ->
                    DateField(field, isLast = index == 2, onDone, Modifier.weight(FIELD_WEIGHTS[index]).yearFocus(index))
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(FIELD_GAP)
            ) {
                fields.forEachIndexed { index, field ->
                    DateField(field, isLast = index == 2, onDone, Modifier.fillMaxWidth().yearFocus(index))
                }
            }
        }
    }
}

@Composable
private fun DateField(field: DateFieldSpec, isLast: Boolean, onDone: () -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = field.value,
        onValueChange = field.onValueChange,
        label = {
            Text(
                field.label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = if (isLast) ImeAction.Done else ImeAction.Next
        ),
        // onNext moves the focus itself (see DateConverter), once
        keyboardActions = KeyboardActions(onNext = { field.onNext() }, onDone = { onDone() }),
        modifier = modifier,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
