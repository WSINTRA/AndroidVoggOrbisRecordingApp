package com.example.audiorecorder.fileUtils

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream

/**
 * Real implementation of FileExporter that uses Android's MediaStore
 */
class RealFileExporter(private val context: Context) : FileExporter {

    companion object {
        private const val ARTIST = "WSINTRA"
        private const val ALBUM = "GRAPHENE_OS"
    }

    override fun exportToMusic(sourceFile: File, displayName: String): ExportResult {
        // Check if source file exists
        if (!sourceFile.exists()) {
            return ExportResult.Error("Source file does not exist")
        }

        return try {
            // Get file extension
            val extension = sourceFile.extension
            val fileName = "$displayName.$extension"

            // Determine MIME type based on extension
            val mimeType = when (extension.lowercase()) {
                "mp3" -> "audio/mpeg"
                "m4a", "aac" -> "audio/aac"
                "wav" -> "audio/wav"
                "ogg" -> "audio/ogg"
                "flac" -> "audio/flac"
                else -> "audio/*"
            }

            // Prepare ContentValues for MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Audio.Media.MIME_TYPE, mimeType)
                put(MediaStore.Audio.Media.ARTIST, ARTIST)
                put(MediaStore.Audio.Media.ALBUM, ALBUM)
                put(MediaStore.Audio.Media.IS_MUSIC, true)
            }

            // Insert into MediaStore
            val uri = context.contentResolver.insert(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return ExportResult.Error("Failed to create MediaStore entry")

            // Copy file content to MediaStore URI
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return ExportResult.Error("Failed to open output stream")

            ExportResult.Success(uri)

        } catch (e: Exception) {
            ExportResult.Error("Export failed: ${e.message}", e)
        }
    }
}