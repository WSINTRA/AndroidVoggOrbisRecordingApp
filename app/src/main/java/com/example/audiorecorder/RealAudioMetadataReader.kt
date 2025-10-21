package com.example.audiorecorder

import android.media.MediaMetadataRetriever
import java.io.File

/**
 * Real implementation of AudioMetadataReader using Android's MediaMetadataRetriever
 */
class RealAudioMetadataReader(
    private val retrieverFactory: MediaMetadataRetrieverFactory
) : AudioMetadataReader {

    override fun getDuration(file: File): Long {
        val retriever = retrieverFactory.create()

        return try {
            retriever.setDataSource(file.absolutePath)
            val durationString = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )
            durationString?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            // Return 0 if we can't read the file
            println("Error reading file: ${file.absolutePath}")
            e.printStackTrace()
            0L
        } finally {
            // Always release the retriever
            try {
                retriever.release()
            } catch (e: Exception) {
                println("Error releasing retriever")
                e.printStackTrace()
                // Ignore release errors
            }
        }
    }
}