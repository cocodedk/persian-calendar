package com.cocode.calendar.components.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.calendar.CalColors
import com.cocode.calendar.Event
import com.cocode.calendar.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Form components for event creation and editing.
 * Contains form fields, state management, and validation logic.
 */
@Composable
fun EventFormFields(
    dialogDate: LocalDate,
    isEditMode: Boolean,
    eventToEdit: com.cocode.calendar.Event?,
    onSaveEvent: (EventFormData) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(if (isEditMode) eventToEdit?.title ?: "" else "") }
    var description by remember { mutableStateOf(if (isEditMode) eventToEdit?.description ?: "" else "") }
    var isRepeating by remember { mutableStateOf(if (isEditMode) eventToEdit?.isRepeating ?: false else false) }
    var repetitionEndYear by remember { mutableStateOf("") }

    // Reset states when switching between create and edit modes
    LaunchedEffect(isEditMode, eventToEdit) {
        eventToEdit?.let { event ->
            if (isEditMode) {
                title = event.title
                description = event.description ?: ""
                isRepeating = event.isRepeating
                repetitionEndYear = event.repetitionEndDate?.let {
                    LocalDate.parse(it).year.toString()
                } ?: ""
            } else {
                title = ""
                description = ""
                isRepeating = false
                repetitionEndYear = ""
            }
        } ?: run {
            if (!isEditMode) {
                title = ""
                description = ""
                isRepeating = false
                repetitionEndYear = ""
            }
        }
    }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = stringResource(
                if (isEditMode) R.string.event_form_edit_title else R.string.event_form_create_title
            ),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = CalColors.background
        )

        // Date display
        Text(
            text = stringResource(
                R.string.event_form_date,
                dialogDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        // Title input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.event_form_title_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CalColors.background,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = CalColors.background,
                unfocusedLabelColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = CalColors.background
            )
        )

        // Description input
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.event_form_description_label)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CalColors.background,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = CalColors.background,
                unfocusedLabelColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = CalColors.background
            )
        )

        // Yearly repetition checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isRepeating,
                onCheckedChange = { isRepeating = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = CalColors.background,
                    uncheckedColor = Color.Gray
                )
            )
            Text(
                text = stringResource(R.string.event_form_repeat_yearly),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }

        // Repetition end year input (only show when repeating is enabled)
        if (isRepeating) {
            OutlinedTextField(
                value = repetitionEndYear,
                onValueChange = { repetitionEndYear = it },
                label = { Text(stringResource(R.string.event_form_end_year_label)) },
                placeholder = { Text(stringResource(R.string.event_form_end_year_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CalColors.background,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = CalColors.background,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = CalColors.background
                )
            )

            Text(
                text = stringResource(R.string.event_form_end_year_hint),
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }

        // Form validation and save logic
        val formData = EventFormData(
            title = title.trim(),
            description = if (description.isBlank()) null else description.trim(),
            isRepeating = isRepeating,
            repetitionEndYear = repetitionEndYear.trim()
        )

        val validationResult = validateEventForm(formData)
        val isFormValid = validationResult.isValid

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cancel button
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.action_cancel), color = Color.White)
            }

            // Create/Update button
            Button(
                onClick = {
                    if (isFormValid) {
                        onSaveEvent(formData)
                    }
                },
                modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = CalColors.button_background
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
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

        // Show validation errors if any
        validationResult.errors.forEach { error ->
            androidx.compose.material3.Text(
                text = stringResource(error),
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Data class representing the form data for event creation/editing.
 */
data class EventFormData(
    val title: String,
    val description: String?,
    val isRepeating: Boolean,
    val repetitionEndYear: String
)
