package com.cocode.calendar.components.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.R

/**
 * Confirmation dialog shown before an event is deleted. It watches the view model, so it
 * shows itself when a delete is requested from the event list.
 */
@Composable
fun DeleteEventDialog() {
    val viewModel: CalendarViewModel = viewModel()
    val showDeleteDialog by viewModel.showDeleteConfirmationDialog.collectAsState()
    val eventToDelete by viewModel.eventToDelete.collectAsState()

    if (showDeleteDialog && eventToDelete != null) {
        Dialog(onDismissRequest = { viewModel.hideDeleteConfirmationDialog() }) {
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
                        text = stringResource(R.string.event_delete_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CalColors.background
                    )

                    // Confirmation message
                    Text(
                        text = stringResource(
                            // A repeating event is one record, so deleting it removes every repeat
                            if (eventToDelete!!.isRepeating) R.string.event_delete_message_repeating
                            else R.string.event_delete_message,
                            eventToDelete!!.title
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray
                    )

                    Text(
                        text = stringResource(R.string.event_delete_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red.copy(alpha = 0.7f)
                    )

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel button
                        Button(
                            onClick = { viewModel.hideDeleteConfirmationDialog() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.action_cancel), color = Color.White)
                        }

                        // Delete button
                        Button(
                            onClick = { viewModel.confirmDeleteEvent() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.action_delete), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
