package com.example.audiorecorder.fileUtils

import android.content.Context
import java.io.File

/**
 * Real implementation of FileProvider that uses Android's file system
 */
class RealFileProvider(private val context: Context) : FileProvider {

    companion object {
        private const val RECORDINGS_DIR = "Recordings"
    }

    override fun getRecordingsDirectory(): File {
        val externalFilesDir = context.getExternalFilesDir(null)
            ?: throw IllegalStateException("External files directory not available")

        val recordingsDir = File(externalFilesDir, RECORDINGS_DIR)

        // Create directory if it doesn't exist
        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs()
        }

        return recordingsDir
    }
}