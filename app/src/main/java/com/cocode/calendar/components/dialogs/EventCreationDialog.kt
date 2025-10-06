package com.cocode.calendar.components.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.Event
import com.cocode.calendar.components.forms.EventFormFields
import com.cocode.calendar.components.forms.EventFormData
import com.cocode.calendar.components.forms.validateEventForm
import java.time.LocalDate

/**
 * Dialog component for creating and editing events.
 * This component handles the dialog presentation and delegates form logic to EventFormComponents.
 */
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
                EventFormFields(
                    dialogDate = dialogDate,
                    isEditMode = isEditMode,
                    eventToEdit = eventToEdit,
                    onSaveEvent = { formData ->
                        if (validateEventForm(formData).isValid) {
                            handleSaveEvent(
                                formData = formData,
                                dialogDate = dialogDate,
                                isEditMode = isEditMode,
                                eventToEdit = eventToEdit,
                                viewModel = viewModel
                            )
                        }
                    },
                    onCancel = {
                        if (isEditMode) viewModel.hideEventEditDialog()
                        else viewModel.hideEventCreationDialog()
                    }
                )
            }
        }
    }
}

/**
 * Handles the save event logic for both create and edit modes.
 */
private fun handleSaveEvent(
    formData: EventFormData,
    dialogDate: LocalDate,
    isEditMode: Boolean,
    eventToEdit: com.cocode.calendar.Event?,
    viewModel: CalendarViewModel
) {
    val endDate = if (formData.isRepeating && formData.repetitionEndYear.isNotBlank()) {
        try {
            LocalDate.of(formData.repetitionEndYear.toInt(), 12, 31)
        } catch (e: Exception) {
            null
        }
    } else null

    if (isEditMode) {
        eventToEdit?.let { event ->
            viewModel.updateEvent(
                event = event,
                title = formData.title,
                description = formData.description,
                startDate = dialogDate,
                endDate = dialogDate,
                color = "BLUE",
                isAllDay = true,
                isRepeating = formData.isRepeating,
                repetitionType = if (formData.isRepeating) Event.REPETITION_YEARLY else Event.REPETITION_NONE,
                repetitionEndDate = endDate
            )
        }
        viewModel.hideEventEditDialog()
    } else {
        viewModel.addEvent(
            title = formData.title,
            description = formData.description,
            startDate = dialogDate,
            endDate = dialogDate,
            color = "BLUE",
            isAllDay = true,
            isRepeating = formData.isRepeating,
            repetitionType = if (formData.isRepeating) Event.REPETITION_YEARLY else Event.REPETITION_NONE,
            repetitionEndDate = endDate
        )
        viewModel.hideEventCreationDialog()
    }
}
