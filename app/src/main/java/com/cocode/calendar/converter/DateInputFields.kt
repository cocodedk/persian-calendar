package com.cocode.calendar.converter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
    onMonthDone: () -> Unit,
    onDayDone: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val yearFocusRequester = remember { FocusRequester() }

    // Auto-focus the Year field when the component is first displayed
    LaunchedEffect(Unit) {
        yearFocusRequester.requestFocus()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            Triple("Year", year) { input: String ->
                if (input.isEmpty() || input.length <= 4 && input.all { it.isDigit() }) {
                    onYearChange(input)
                }
            },
            Triple("Mo", month) { input: String ->
                val num = input.toIntOrNull()
                if (input.isEmpty() || input.length <= 2 && num != null && num in 1..12) {
                    onMonthChange(input)
                }
            },
            Triple("Day", day) { input: String ->
                val num = input.toIntOrNull()
                if (input.isEmpty() || input.length <= 2 && num != null && num in 1..31) {
                    onDayChange(input)
                }
            }
        ).forEachIndexed { index, (label, value, onValueChange) ->
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == 2) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        when (index) {
                            0 -> {
                                onYearDone()
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                            1 -> {
                                onMonthDone()
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        }
                    },
                    onDone = {
                        keyboardController?.hide()
                        if (index == 2) {
                            onDayDone()
                            focusManager.clearFocus()
                        }
                    }
                ),
                modifier = Modifier
                    .weight(if (index == 0) 1.5f else 1f)
                    .let { if (index == 0) it.focusRequester(yearFocusRequester) else it },
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
    }
}
