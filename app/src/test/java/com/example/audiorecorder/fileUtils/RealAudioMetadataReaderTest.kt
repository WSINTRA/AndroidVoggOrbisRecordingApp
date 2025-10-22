package com.example.audiorecorder.fileUtils

import android.media.MediaMetadataRetriever
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class RealAudioMetadataReaderTest {

    private lateinit var mockRetriever: MediaMetadataRetriever
    private lateinit var mockRetrieverFactory: MediaMetadataRetrieverFactory
    private lateinit var audioMetadataReader: RealAudioMetadataReader

    @Before
    fun setup() {
        mockRetriever = mockk(relaxed = true)
        mockRetrieverFactory = mockk()
        every { mockRetrieverFactory.create() } returns mockRetriever
        audioMetadataReader = RealAudioMetadataReader(mockRetrieverFactory)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun shouldReturnDurationInMilliseconds() {
        // Arrange
        val file = mockk<File>(relaxed = true) {
            every { absolutePath } returns "/path/to/audio.m4a"
        }
        every {
            mockRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        } returns "5000" // 5 seconds

        // Act
        val duration = audioMetadataReader.getDuration(file)

        // Assert
        Assert.assertEquals(5000L, duration)
        verify { mockRetriever.release() }
    }

    @Test
    fun shouldReturnZeroWhenDurationIsNull() {
        // Arrange
        val file = mockk<File>(relaxed = true) {
            every { absolutePath } returns "/path/to/audio.m4a"
        }
        every {
            mockRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        } returns null

        // Act
        val duration = audioMetadataReader.getDuration(file)

        // Assert
        Assert.assertEquals(0L, duration)
        verify { mockRetriever.release() }
    }

    @Test
    fun shouldReturnZeroWhenDurationIsInvalid() {
        // Arrange
        val file = mockk<File>(relaxed = true) {
            every { absolutePath } returns "/path/to/audio.m4a"
        }
        every {
            mockRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        } returns "invalid"

        // Act
        val duration = audioMetadataReader.getDuration(file)

        // Assert
        Assert.assertEquals(0L, duration)
        verify { mockRetriever.release() }
    }

    @Test
    fun shouldReleaseRetrieverEvenOnException() {
        // Arrange
        val file = mockk<File>(relaxed = true) {
            every { absolutePath } returns "/path/to/audio.m4a"
        }
        every { mockRetriever.setDataSource(any<String>()) } throws RuntimeException("File not found")

        // Act
        val duration = audioMetadataReader.getDuration(file)

        // Assert
        Assert.assertEquals(0L, duration)
        verify { mockRetriever.release() }
    }
}