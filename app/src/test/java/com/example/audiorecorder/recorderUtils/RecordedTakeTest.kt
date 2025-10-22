package com.example.audiorecorder

import com.example.audiorecorder.recorderUtils.RecordedTake
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import java.util.Date

class RecordedTakeTest {

    @Test
    fun shouldCreateRecordedTakeWithAllProperties() {
        // Arrange
        val file = File("/path/to/audio.m4a")
        val duration = 5000L // 5 seconds in milliseconds
        val dateRecorded = Date()

        // Act
        val recordedTake = RecordedTake(
            file = file,
            duration = duration,
            dateRecorded = dateRecorded
        )

        // Assert
        assertEquals(file, recordedTake.file)
        assertEquals(duration, recordedTake.duration)
        assertEquals(dateRecorded, recordedTake.dateRecorded)
    }

    @Test
    fun shouldGetFilenameFromFile() {
        // Arrange
        val file = File("/path/to/recording_123.m4a")
        val recordedTake = RecordedTake(
            file = file,
            duration = 1000L,
            dateRecorded = Date()
        )

        // Act
        val filename = recordedTake.filename

        // Assert
        assertEquals("recording_123.m4a", filename)
    }

    @Test
    fun shouldFormatDurationInMinutesAndSeconds() {
        // Arrange
        val recordedTake = RecordedTake(
            file = File("/path/to/audio.m4a"),
            duration = 125000L, // 2 minutes 5 seconds
            dateRecorded = Date()
        )

        // Act
        val formattedDuration = recordedTake.formattedDuration

        // Assert
        assertEquals("2:05", formattedDuration)
    }

    @Test
    fun shouldFormatDurationWithLeadingZeroForSeconds() {
        // Arrange
        val recordedTake = RecordedTake(
            file = File("/path/to/audio.m4a"),
            duration = 63000L, // 1 minute 3 seconds
            dateRecorded = Date()
        )

        // Act
        val formattedDuration = recordedTake.formattedDuration

        // Assert
        assertEquals("1:03", formattedDuration)
    }

    @Test
    fun shouldFormatDurationForZeroMinutes() {
        // Arrange
        val recordedTake = RecordedTake(
            file = File("/path/to/audio.m4a"),
            duration = 45000L, // 45 seconds
            dateRecorded = Date()
        )

        // Act
        val formattedDuration = recordedTake.formattedDuration

        // Assert
        assertEquals("0:45", formattedDuration)
    }
}