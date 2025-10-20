package com.example.audiorecorder

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class AudioRecorderTest {
    // Collaborators we’ll mock. Engine is relaxed so we don’t have to stub Unit-returning methods.
    private val engine: RecorderEngine = mockk(relaxed = true)
    private val outputFactory: OutputFileFactory = mockk()

    // System under test; doesn’t exist yet (expected compile failure in Red).
    private val recorder = AudioRecorder(engine, outputFactory)

    @Test
    fun `start should prepare engine then start and return the expected file`() {
        // Arrange: the file the recorder should write to.
        val expected = File("recording_123.mp4")
        every { outputFactory.nextFile() } returns expected

        // Act
        val result = recorder.start()

        // Assert: verify behavior and ordering (factory -> prepare(file) -> start()).
        assertEquals(expected, result)
        verifyOrder {
            outputFactory.nextFile()
            engine.prepare(expected)
            engine.start()
        }
        confirmVerified(engine, outputFactory)
    }

}