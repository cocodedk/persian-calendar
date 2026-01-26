package com.cocode.calendar.components.time

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.calendar.CalColors
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import utils.DateTimeUtils
import java.time.format.DateTimeFormatter

/**
 * Component for displaying the current time in Iran timezone.
 * This component handles the ticking clock functionality independently.
 */
@Composable
fun DisplayTimeInIran() {
    // This composable function displays the current time in Iran.
    // This state is remembered across recompositions
    val currentTime = remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = Unit) {
        while (currentCoroutineContext().isActive) {
            val iranTime = DateTimeUtils.getCurrentTimeInIran()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val formattedIranTime = iranTime.format(formatter)
            currentTime.value = formattedIranTime
            delay(1000)
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        // put the text in a row and center it
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Iran time: ${currentTime.value}",
                style = MaterialTheme.typography.bodyLarge,
                color = CalColors.text
            )
        }
    }
}
