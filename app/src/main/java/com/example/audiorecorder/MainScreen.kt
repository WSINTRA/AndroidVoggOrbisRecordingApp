package com.example.audiorecorder

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

/**
 * Main screen composable containing recording controls and recordings list
 */
@Composable
fun MainScreen(
    recordedTakes: List<RecordedTake>,
    isRecording: Boolean,
    currentlyPlayingTake: RecordedTake?,
    isPlaying: Boolean,
    onStartRecordingClick: () -> Unit,
    onStopRecordingClick: () -> Unit,
    onPlayClick: (RecordedTake) -> Unit,
    onDeleteClick: (RecordedTake) -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var elapsedTime by remember { mutableStateOf(0L) }

    // Timer effect
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val startTime = System.currentTimeMillis()
            while (isRecording) {
                elapsedTime = System.currentTimeMillis() - startTime
                delay(100) // Update every 100ms
            }
        } else {
            elapsedTime = 0L
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Recording controls at the top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Check permission status
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            Spacer(modifier = Modifier.height(32.dp))
            // Recording Timer
            if (isRecording) {
                RecordingTimer(
                    elapsedTimeMillis = elapsedTime,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Start/Stop Button
            Button(
                onClick = {
                    if (!isRecording) {
                        if (permissionGranted) {
                            onStartRecordingClick()
                        } else {
                            onRequestPermission()
                        }
                    } else {
                        onStopRecordingClick()
                    }
                },
                enabled = !isRecording || permissionGranted
            ) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            // Status Text
            if (isRecording) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recording...",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Divider
        HorizontalDivider()

        // List of recordings
        RecordedTakesList(
            takes = recordedTakes,
            onPlayClick = onPlayClick,
            onDeleteClick = onDeleteClick,
            modifier = Modifier.weight(1f)
        )
    }
}