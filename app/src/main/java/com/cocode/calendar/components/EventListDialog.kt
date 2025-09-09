package com.cocode.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalColors
import com.cocode.calendar.CalendarViewModel
import com.cocode.calendar.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EventListDialog() {
    val viewModel: CalendarViewModel = viewModel()
    val showDialog by viewModel.showEventListDialog.collectAsState()
    val selectedDate by viewModel.eventListSelectedDate.collectAsState()

    if (showDialog && selectedDate != null) {
        val eventsForDate by viewModel.getEventsForDateOnly(selectedDate!!).collectAsState()

        Dialog(onDismissRequest = { viewModel.hideEventListDialog() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Events",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = CalColors.active_text
                            )
                            Text(
                                text = selectedDate!!.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                style = MaterialTheme.typography.bodyMedium,
                                color = CalColors.inactive_text
                            )
                        }

                        // Add new event button
                        FloatingActionButton(
                            onClick = { viewModel.showEventCreationFromEventList(selectedDate!!) },
                            modifier = Modifier.size(48.dp),
                            containerColor = CalColors.button_background,
                            contentColor = CalColors.text
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Event")
                        }
                    }

                    Divider(color = Color.Gray.copy(alpha = 0.3f))

                    // Events list
                    if (eventsForDate.isEmpty()) {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "No events for this day",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = CalColors.inactive_text
                                )
                                Button(
                                    onClick = { viewModel.showEventCreationFromEventList(selectedDate!!) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CalColors.button_background
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Create First Event", color = CalColors.text)
                                }
                            }
                        }
                    } else {
                        // Events list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(eventsForDate) { event ->
                                EventItemCard(
                                    event = event,
                                    onEditEvent = { viewModel.showEventEditDialog(it) },
                                    onDeleteEvent = { viewModel.showDeleteConfirmationDialog(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
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
                        text = "Delete Event",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CalColors.active_text
                    )

                    // Confirmation message
                    Text(
                        text = "Are you sure you want to delete \"${eventToDelete!!.title}\"?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CalColors.inactive_text
                    )

                    Text(
                        text = "This action cannot be undone.",
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
                            Text("Cancel", color = Color.White)
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
                            Text("Delete", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventItemCard(
    event: Event,
    onEditEvent: (Event) -> Unit,
    onDeleteEvent: (Event) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CalColors.active_text,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!event.description.isNullOrBlank()) {
                    Text(
                        text = event.description!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CalColors.inactive_text,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (event.isAllDay) {
                    Text(
                        text = "All Day",
                        style = MaterialTheme.typography.bodySmall,
                        color = CalColors.button_background,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Show repetition info
                if (event.isRepeating && event.repetitionType == "YEARLY") {
                    Text(
                        text = if (event.repetitionEndDate != null) {
                            "Repeats yearly until ${LocalDate.parse(event.repetitionEndDate!!).year}"
                        } else {
                            "Repeats yearly"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Edit and Delete buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onEditEvent(event) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Event",
                        tint = CalColors.button_background,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { onDeleteEvent(event) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Event",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
