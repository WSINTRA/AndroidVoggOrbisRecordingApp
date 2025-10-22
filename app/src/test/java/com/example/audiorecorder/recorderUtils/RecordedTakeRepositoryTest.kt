package com.example.audiorecorder

import com.example.audiorecorder.fileUtils.AudioMetadataReader
import com.example.audiorecorder.fileUtils.FileProvider
import com.example.audiorecorder.recorderUtils.RecordedTake
import com.example.audiorecorder.fileUtils.RecordedTakesRepository
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import java.util.Date

class RecordedTakesRepositoryTest {

    private lateinit var repository: RecordedTakesRepository
    private lateinit var mockFileProvider: FileProvider
    private lateinit var mockAudioMetadataReader: AudioMetadataReader

    @Before
    fun setup() {
        mockFileProvider = mockk()
        mockAudioMetadataReader = mockk()
        repository = RecordedTakesRepository(mockFileProvider, mockAudioMetadataReader)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun shouldReturnEmptyListWhenNoRecordingsExist() {
        // Arrange
        every { mockFileProvider.getRecordingsDirectory() } returns mockk {
            every { listFiles() } returns emptyArray()
        }

        // Act
        val takes = repository.getAllTakes()

        // Assert
        assertTrue(takes.isEmpty())
    }

    @Test
    fun shouldReturnListOfRecordedTakes() {
        // Arrange
        val file1 = mockk<File>(relaxed = true) {
            every { name } returns "recording1.m4a"
            every { path } returns "/path/recording1.m4a"
            every { isFile } returns true
            every { lastModified() } returns 1000000L
        }
        val file2 = mockk<File>(relaxed = true) {
            every { name } returns "recording2.m4a"
            every { path } returns "/path/recording2.m4a"
            every { isFile } returns true
            every { lastModified() } returns 2000000L
        }

        every { mockFileProvider.getRecordingsDirectory() } returns mockk {
            every { listFiles() } returns arrayOf(file1, file2)
        }
        every { mockAudioMetadataReader.getDuration(file1) } returns 5000L
        every { mockAudioMetadataReader.getDuration(file2) } returns 10000L

        // Act
        val takes = repository.getAllTakes()

        // Assert
        assertEquals(2, takes.size)
        // Should be sorted by date descending, so file2 (newer) comes first
        assertEquals("recording2.m4a", takes[0].filename)
        assertEquals("recording1.m4a", takes[1].filename)
        assertEquals(10000L, takes[0].duration)
        assertEquals(5000L, takes[1].duration)
    }

    @Test
    fun shouldSortTakesByDateDescending() {
        // Arrange - file2 is older, file1 is newer
        val file1 = mockk<File>(relaxed = true) {
            every { name } returns "recording1.m4a"
            every { path } returns "/path/recording1.m4a"
            every { isFile } returns true
            every { lastModified() } returns 2000000L // newer
        }
        val file2 = mockk<File>(relaxed = true) {
            every { name } returns "recording2.m4a"
            every { path } returns "/path/recording2.m4a"
            every { isFile } returns true
            every { lastModified() } returns 1000000L // older
        }

        every { mockFileProvider.getRecordingsDirectory() } returns mockk {
            every { listFiles() } returns arrayOf(file2, file1) // unsorted order
        }
        every { mockAudioMetadataReader.getDuration(any()) } returns 5000L

        // Act
        val takes = repository.getAllTakes()

        // Assert
        assertEquals(2, takes.size)
        assertEquals("recording1.m4a", takes[0].filename) // newer first
        assertEquals("recording2.m4a", takes[1].filename) // older second
    }

    @Test
    fun shouldFilterOutNonAudioFiles() {
        // Arrange
        val audioFile = mockk<File>(relaxed = true) {
            every { name } returns "recording.m4a"
            every { path } returns "/path/recording.m4a"
            every { isFile } returns true
            every { lastModified() } returns 1000000L
        }
        val textFile = mockk<File>(relaxed = true) {
            every { name } returns "notes.txt"
            every { isFile } returns true
        }

        every { mockFileProvider.getRecordingsDirectory() } returns mockk {
            every { listFiles() } returns arrayOf(audioFile, textFile)
        }
        every { mockAudioMetadataReader.getDuration(audioFile) } returns 5000L

        // Act
        val takes = repository.getAllTakes()

        // Assert
        assertEquals(1, takes.size)
        assertEquals("recording.m4a", takes[0].filename)
    }

    @Test
    fun shouldDeleteTakeAndReturnTrue() {
        // Arrange
        val file = mockk<File>(relaxed = true) {
            every { delete() } returns true
        }
        val take = RecordedTake(file, 5000L, Date())

        // Act
        val result = repository.deleteTake(take)

        // Assert
        assertTrue(result)
        verify { file.delete() }
    }

    @Test
    fun shouldReturnFalseWhenDeleteFails() {
        // Arrange
        val file = mockk<File>(relaxed = true) {
            every { delete() } returns false
        }
        val take = RecordedTake(file, 5000L, Date())

        // Act
        val result = repository.deleteTake(take)

        // Assert
        assertFalse(result)
        verify { file.delete() }
    }
}