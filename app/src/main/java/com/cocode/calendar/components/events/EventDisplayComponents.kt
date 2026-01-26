package com.cocode.calendar.components.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cocode.calendar.CalColors
import java.time.LocalDate

/**
 * Displays an individual event in a card format with edit and delete actions.
 * This component handles the visual representation of a single event.
 */
@Composable
fun EventItemCard(
    event: com.cocode.calendar.Event,
    onEditEvent: (com.cocode.calendar.Event) -> Unit,
    onDeleteEvent: (com.cocode.calendar.Event) -> Unit
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

                val description = event.description
                if (!description.isNullOrBlank()) {
                    Text(
                        text = description,
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
                    val repetitionEndDate = event.repetitionEndDate
                    Text(
                        text = if (repetitionEndDate != null) {
                            "Repeats yearly until ${LocalDate.parse(repetitionEndDate).year}"
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
