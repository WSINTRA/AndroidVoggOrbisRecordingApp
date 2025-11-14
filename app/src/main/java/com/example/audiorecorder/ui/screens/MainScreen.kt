package com.example.audiorecorder.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.audiorecorder.playerUtils.AudioEffectProcessor
import com.example.audiorecorder.recorderUtils.RecordedTake
import com.example.audiorecorder.ui.components.RecordedTakesList
import com.example.audiorecorder.ui.components.ThreeBandEqControl
import com.example.audiorecorder.ui.components.ThreeBandEqState
import com.example.audiorecorder.ui.components.TimerUI
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
    audioEffectProcessor: AudioEffectProcessor?,
    onStartRecordingClick: () -> Unit,
    onStopRecordingClick: () -> Unit,
    onPlayClick: (RecordedTake) -> Unit,
    onDeleteClick: (RecordedTake) -> Unit,
    onExportClick: (RecordedTake) -> Unit,
    onShareClick: (RecordedTake) -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var recordingElapsedTime by remember { mutableStateOf(0L) }
    var playbackElapsedTime by remember { mutableStateOf(0L) }


    // EQ state - default neutral position (5 out of 10)
    var eqState by remember {
        mutableStateOf(
            ThreeBandEqState(
                lowGain = 5f,
                midGain = 5f,
                highGain = 5f
            )
        )
    }

    // Recording Timer effect
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val startTime = System.currentTimeMillis()
            while (isRecording) {
                recordingElapsedTime = System.currentTimeMillis() - startTime
                delay(100) // Update every 100ms
            }
        } else {
            recordingElapsedTime = 0L
        }
    }

    // Playback Timer effect
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val startTime = System.currentTimeMillis()
            while (isPlaying) {
                playbackElapsedTime = System.currentTimeMillis() - startTime
                delay(100) // Update every 100ms
            }
        } else {
            playbackElapsedTime = 0L
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

            Spacer(modifier = Modifier.height(40.dp))

            if(!isRecording && !isPlaying) TimerUI(
                elapsedTimeMillis = recordingElapsedTime,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Recording Timer
            if (isRecording) {
                TimerUI(
                    elapsedTimeMillis = recordingElapsedTime,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            // Playback Timer
            if (isPlaying) {
                TimerUI(
                    elapsedTimeMillis = playbackElapsedTime,
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
            currentlyPlayingTake = currentlyPlayingTake,
            takes = recordedTakes,
            onPlayClick = onPlayClick,
            onDeleteClick = onDeleteClick,
            onExportClick = onExportClick,
            onShareClick = onShareClick,
            modifier = Modifier.weight(1f)

        )

        // EQ Controls - shown only when playing

        HorizontalDivider()
        ThreeBandEqControl(
            state = eqState,
            enabled = isPlaying,
            onStateChange = { newState ->
                eqState = newState
                // Apply EQ changes to audio processor
                audioEffectProcessor?.applyEqSettings(newState)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("eq_controls")
        )

    }
}