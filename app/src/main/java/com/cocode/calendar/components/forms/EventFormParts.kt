package com.cocode.calendar.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cocode.calendar.CalColors
import com.cocode.calendar.R

/**
 * Data class representing the form data for event creation/editing.
 */
data class EventFormData(
    val title: String,
    val description: String?,
    val isRepeating: Boolean,
    val repetitionEndYear: String
)

/**
 * Colours shared by the text fields of the event form (title, description, last year).
 */
@Composable
fun eventFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CalColors.background,
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor = CalColors.background,
    unfocusedLabelColor = Color.Gray,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = CalColors.background
)

/**
 * The Cancel and Create/Update buttons at the bottom of the event form.
 * The save button is disabled while the form is not valid.
 */
@Composable
fun EventFormButtons(
    isEditMode: Boolean,
    isFormValid: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FormButton(onClick = onCancel, containerColor = Color.Gray) {
            Text(stringResource(R.string.action_cancel), color = Color.White)
        }

        FormButton(
            onClick = { if (isFormValid) onSave() },
            containerColor = CalColors.button_background,
            enabled = isFormValid
        ) {
            Text(
                text = stringResource(
                    if (isEditMode) R.string.event_form_update else R.string.event_form_create
                ),
                color = CalColors.text
            )
        }
    }
}

@Composable
private fun RowScope.FormButton(
    onClick: () -> Unit,
    containerColor: Color,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled
    ) {
        content()
    }
}
