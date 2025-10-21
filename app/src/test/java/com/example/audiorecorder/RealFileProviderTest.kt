package com.example.audiorecorder

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.File

class RealFileProviderTest {

    private lateinit var mockContext: Context
    private lateinit var fileProvider: RealFileProvider

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        fileProvider = RealFileProvider(mockContext)
    }

    @Test
    fun shouldReturnRecordingsDirectory() {
        // Arrange
        val expectedDir = File("/mock/files/Recordings")
        every { mockContext.getExternalFilesDir(null) } returns File("/mock/files")

        // Act
        val result = fileProvider.getRecordingsDirectory()

        // Assert
        assertEquals("Recordings", result.name)
        assertTrue(result.path.endsWith("Recordings"))
    }

    @Test
    fun shouldCreateRecordingsDirectoryIfNotExists() {
        // Arrange
        val filesDir = File.createTempFile("test", "dir").parentFile
        val recordingsDir = File(filesDir, "Recordings")

        // Clean up if exists
        recordingsDir.deleteRecursively()

        every { mockContext.getExternalFilesDir(null) } returns filesDir

        // Act
        val result = fileProvider.getRecordingsDirectory()

        // Assert
        assertTrue(result.exists())
        assertTrue(result.isDirectory)

        // Cleanup
        recordingsDir.deleteRecursively()
    }
}