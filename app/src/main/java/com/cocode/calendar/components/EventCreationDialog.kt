package com.cocode.calendar.components

import androidx.compose.foundation.background
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EventCreationDialog() {
    val viewModel: CalendarViewModel = viewModel()
    val showDialog by viewModel.showEventCreationDialog.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    if (showDialog && selectedDate != null) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { viewModel.hideEventCreationDialog() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Text(
                        text = "Create Event",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CalColors.active_text
                    )

                    // Date display
                    Text(
                        text = "Date: ${selectedDate!!.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
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

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel button
                        Button(
                            onClick = { viewModel.hideEventCreationDialog() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel", color = Color.White)
                        }

                        // Create button
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    viewModel.addEvent(
                                        title = title.trim(),
                                        description = if (description.isBlank()) null else description.trim(),
                                        startDate = selectedDate!!,
                                        endDate = selectedDate!!,
                                        color = "BLUE",
                                        isAllDay = true
                                    )
                                    viewModel.hideEventCreationDialog()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CalColors.button_background
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = title.isNotBlank()
                        ) {
                            Text("Create", color = CalColors.text)
                        }
                    }
                }
            }
        }
    }
}
