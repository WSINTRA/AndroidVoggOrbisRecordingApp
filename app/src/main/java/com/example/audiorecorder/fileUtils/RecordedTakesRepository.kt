package com.example.audiorecorder.fileUtils

import com.example.audiorecorder.recorderUtils.RecordedTake
import java.io.File
import java.util.Date

/**
 * Repository for managing recorded audio takes
 */
class RecordedTakesRepository(
    private val fileProvider: FileProvider,
    private val audioMetadataReader: AudioMetadataReader
) {
    companion object {
        private val AUDIO_EXTENSIONS = setOf("m4a", "mp4", "aac", "3gp")
    }

    /**
     * Gets all recorded takes, sorted by date (newest first)
     */
    fun getAllTakes(): List<RecordedTake> {
        val recordingsDir = fileProvider.getRecordingsDirectory()
        val files = recordingsDir.listFiles() ?: return emptyList()

        return files
            .filter { it.isFile && it.isAudioFile() }
            .map { file ->
                RecordedTake(
                    file = file,
                    duration = audioMetadataReader.getDuration(file),
                    dateRecorded = Date(file.lastModified())
                )
            }
            .sortedByDescending { it.dateRecorded }
    }

    /**
     * Deletes a recorded take from the file system
     * @return true if deletion was successful, false otherwise
     */
    fun deleteTake(take: RecordedTake): Boolean {
        return take.file.delete()
    }

    private fun File.isAudioFile(): Boolean {
        val extension = this.name.substringAfterLast('.', "").lowercase()
        return extension in AUDIO_EXTENSIONS
    }
}