package com.cocode.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EventCreationDialog() {
    val viewModel: CalendarViewModel = viewModel()
    val showCreateDialog by viewModel.showEventCreationDialog.collectAsState()
    val showEditDialog by viewModel.showEventEditDialog.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val eventToEdit by viewModel.eventToEdit.collectAsState()

    val showDialog = showCreateDialog || showEditDialog
    val isEditMode = showEditDialog && eventToEdit != null
    val dialogDate = if (isEditMode) LocalDate.parse(eventToEdit!!.startDate) else selectedDate

    if (showDialog && dialogDate != null) {
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

        Dialog(onDismissRequest = {
            if (isEditMode) viewModel.hideEventEditDialog()
            else viewModel.hideEventCreationDialog()
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(
                        width = 2.dp,
                        color = CalColors.background,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Text(
                        text = if (isEditMode) "Edit Event" else "Create Event",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CalColors.active_text
                    )

                    // Date display
                    Text(
                        text = "Date: ${dialogDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CalColors.inactive_text
                    )

                    // Title input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Event Title") },
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
                        label = { Text("Description (Optional)") },
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
                            text = "Repeat yearly",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }

                    // Repetition end year input (only show when repeating is enabled)
                    if (isRepeating) {
                        OutlinedTextField(
                            value = repetitionEndYear,
                            onValueChange = { repetitionEndYear = it },
                            label = { Text("End repetition year (Optional)") },
                            placeholder = { Text("e.g., 2030") },
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
                            text = "If specified, the event will repeat every year until this year",
                            style = MaterialTheme.typography.bodySmall,
                            color = CalColors.inactive_text
                        )
                    }

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel button
                        Button(
                            onClick = {
                                if (isEditMode) viewModel.hideEventEditDialog()
                                else viewModel.hideEventCreationDialog()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel", color = Color.White)
                        }

                        // Create/Update button
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val endDate = if (isRepeating && repetitionEndYear.isNotBlank()) {
                                        try {
                                            LocalDate.of(repetitionEndYear.toInt(), 12, 31)
                                        } catch (e: Exception) {
                                            null
                                        }
                                    } else null

                                    if (isEditMode) {
                                        eventToEdit?.let { event ->
                                            viewModel.updateEvent(
                                                event = event,
                                                title = title.trim(),
                                                description = if (description.isBlank()) null else description.trim(),
                                                startDate = dialogDate,
                                                endDate = dialogDate,
                                                color = "BLUE",
                                                isAllDay = true,
                                                isRepeating = isRepeating,
                                                repetitionType = if (isRepeating) Event.REPETITION_YEARLY else Event.REPETITION_NONE,
                                                repetitionEndDate = endDate
                                            )
                                        }
                                        viewModel.hideEventEditDialog()
                                    } else {
                                        viewModel.addEvent(
                                            title = title.trim(),
                                            description = if (description.isBlank()) null else description.trim(),
                                            startDate = dialogDate,
                                            endDate = dialogDate,
                                            color = "BLUE",
                                            isAllDay = true,
                                            isRepeating = isRepeating,
                                            repetitionType = if (isRepeating) Event.REPETITION_YEARLY else Event.REPETITION_NONE,
                                            repetitionEndDate = endDate
                                        )
                                        viewModel.hideEventCreationDialog()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CalColors.button_background
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = title.isNotBlank()
                        ) {
                            Text(
                                text = if (isEditMode) "Update" else "Create",
                                color = CalColors.text
                            )
                        }
                    }
                }
            }
        }
    }
}
