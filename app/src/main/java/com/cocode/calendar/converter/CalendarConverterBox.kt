package com.cocode.calendar.converter

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cocode.calendar.CalendarViewModel

/**
 * Displays a calendar converter box that allows switching between Jalali and Gregorian date converters.
 *
 * This composable function creates a UI element for date conversion as an overlay on top of the calendar.
 * It shows a button to toggle between Jalali to Gregorian and Gregorian to Jalali converters,
 * displays the appropriate converter based on the current state, and includes a close button.
 *
 * @return A composable that displays the calendar converter UI when showConverter is true.
 */
@Composable
fun CalendarConverterBox() {
    val viewModel: CalendarViewModel = viewModel()
    val showConverter by viewModel.showConverter.collectAsState()
    val showJalaliToGregorianConverter by viewModel.showJalaliToGregorianConverter.collectAsState()
    val showGregorianToJalaliConverter by viewModel.showGregorianToJalaliConverter.collectAsState()

    if (showConverter) {
        // Handle Android back button press to close converter
        BackHandler {
            viewModel.toggleConverter()
        }

        // Semi-transparent overlay background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header with title, toggle button, and close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📅 Date Converter",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { viewModel.toggleJalaliToGregorianConverter() },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "⇄",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.toggleConverter() },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close converter"
                                )
                            }
                        }
                    }

                    // Conversion direction indicator
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when {
                                showJalaliToGregorianConverter -> "Jalali → Gregorian"
                                showGregorianToJalaliConverter -> "Gregorian → Jalali"
                                else -> "Error"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    DateConverter(
                        showJalaliToGregorianConverter,
                        showGregorianToJalaliConverter
                    )
                }
            }
        }
    }
}
