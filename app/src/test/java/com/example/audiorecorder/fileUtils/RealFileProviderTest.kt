package com.example.audiorecorder.fileUtils

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
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
        every { mockContext.getExternalFilesDir(null) } returns File("/mock/files")

        // Act
        val result = fileProvider.getRecordingsDirectory()

        // Assert
        Assert.assertEquals("Recordings", result.name)
        Assert.assertTrue(result.path.endsWith("Recordings"))
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
        Assert.assertTrue(result.exists())
        Assert.assertTrue(result.isDirectory)

        // Cleanup
        recordingsDir.deleteRecursively()
    }
}