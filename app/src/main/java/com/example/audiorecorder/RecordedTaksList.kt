package com.example.audiorecorder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Displays a list of recorded takes
 */
@Composable
fun RecordedTakesList(
    takes: List<RecordedTake>,
    onPlayClick: (RecordedTake) -> Unit,
    onDeleteClick: (RecordedTake) -> Unit,
    modifier: Modifier = Modifier
) {
    if (takes.isEmpty()) {
        // Empty state
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No recordings yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        // List of recordings
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(takes) { take ->
                RecordedTakeItem(
                    take = take,
                    onPlayClick = { onPlayClick(take) },
                    onDeleteClick = { onDeleteClick(take) }
                )
            }
        }
    }
}