package com.example.audiorecorder.fileUtils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class RealFileExporterTest {

    private lateinit var context: Context
    private lateinit var fileExporter: RealFileExporter
    private val exportedUris = mutableListOf<Uri>()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        fileExporter = RealFileExporter(context)
    }

    @After
    fun cleanup() {
        // Clean up any exported files
        exportedUris.forEach { uri ->
            try {
                context.contentResolver.delete(uri, null, null)
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
        exportedUris.clear()
    }

    @Test
    fun shouldReturnErrorWhenSourceFileDoesNotExist() {
        // Arrange
        val nonExistentFile = File(context.cacheDir, "nonexistent.mp3")
        val displayName = "exported_audio"

        // Act
        val result = fileExporter.exportToMusic(nonExistentFile, displayName)

        // Assert
        assertTrue(result is ExportResult.Error)
        val error = result as ExportResult.Error
        assertTrue(error.message.contains("does not exist", ignoreCase = true))
    }

    @Test
    fun shouldExportFileToMusicFolderSuccessfully() {
        // Arrange - Create a real test file
        val sourceFile = File(context.cacheDir, "test_audio.mp3")
        sourceFile.writeText("test audio content")

        val displayName = "exported_audio_test"

        // Act
        val result = fileExporter.exportToMusic(sourceFile, displayName)

        // Assert
        assertTrue("Expected Success but got: $result", result is ExportResult.Success)

        val success = result as ExportResult.Success
        assertNotNull(success.uri)

        // Track for cleanup
        exportedUris.add(success.uri)

        // Verify the file was actually created in MediaStore
        val cursor = context.contentResolver.query(
            success.uri,
            arrayOf(MediaStore.Audio.Media.DISPLAY_NAME),
            null,
            null,
            null
        )

        assertNotNull(cursor)
        cursor?.use {
            assertTrue(it.moveToFirst())
            val displayNameIndex = it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            val actualDisplayName = it.getString(displayNameIndex)
            assertEquals("exported_audio_test.mp3", actualDisplayName)
        }

        // Cleanup temp file
        sourceFile.delete()
    }
}