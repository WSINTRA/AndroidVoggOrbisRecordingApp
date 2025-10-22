package com.example.audiorecorder.recorderUtils

import java.io.File
import java.util.Date

/**
 * Represents a recorded audio take with metadata
 * @param file The audio file on disk
 * @param duration Duration in milliseconds
 * @param dateRecorded When the recording was created
 */
data class RecordedTake(
    val file: File,
    val duration: Long,
    val dateRecorded: Date
) {
    /**
     * Gets the filename without the full path
     */
    val filename: String
        get() = file.name

    /**
     * Formats duration as MM:SS
     */
    val formattedDuration: String
        get() {
            val totalSeconds = duration / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}