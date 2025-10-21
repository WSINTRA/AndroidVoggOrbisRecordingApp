package com.example.audiorecorder

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily

/**
 * Displays a recording timer in MM:SS or HH:MM:SS format
 */
@Composable
fun RecordingTimer(
    elapsedTimeMillis: Long,
    modifier: Modifier = Modifier
) {
    val formattedTime = formatElapsedTime(elapsedTimeMillis)

    Text(
        text = formattedTime,
        style = MaterialTheme.typography.headlineMedium,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

/**
 * Formats elapsed time in milliseconds to HH:MM:SS or MM:SS
 */
@SuppressLint("DefaultLocale")
private fun formatElapsedTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}