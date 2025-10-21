package com.example.audiorecorder

import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.File

class AudioPlayerTest {

    private lateinit var mockPlayerEngine: PlayerEngine
    private lateinit var audioPlayer: AudioPlayer

    @Before
    fun setup() {
        mockPlayerEngine = mockk(relaxed = true)
        audioPlayer = AudioPlayer(mockPlayerEngine)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun shouldStartPlayback() {
        // Arrange
        val file = File("/path/to/audio.m4a")

        // Act
        audioPlayer.play(file)

        // Assert
        verify { mockPlayerEngine.setDataSource(file.absolutePath) }
        verify { mockPlayerEngine.prepare() }
        verify { mockPlayerEngine.start() }
    }

    @Test
    fun shouldPausePlayback() {
        // Arrange
        val file = File("/path/to/audio.m4a")
        audioPlayer.play(file)

        // Act
        audioPlayer.pause()

        // Assert
        verify { mockPlayerEngine.pause() }
    }

    @Test
    fun shouldResumePlayback() {
        // Arrange
        val file = File("/path/to/audio.m4a")
        audioPlayer.play(file)
        audioPlayer.pause()

        // Act
        audioPlayer.resume()

        // Assert
        verify(exactly = 2) { mockPlayerEngine.start() }
    }

    @Test
    fun shouldStopPlayback() {
        // Arrange
        val file = File("/path/to/audio.m4a")
        audioPlayer.play(file)

        // Act
        audioPlayer.stop()

        // Assert
        verify { mockPlayerEngine.stop() }
        verify { mockPlayerEngine.reset() }
    }

    @Test
    fun shouldSeekToPosition() {
        // Arrange
        val file = File("/path/to/audio.m4a")
        audioPlayer.play(file)

        // Act
        audioPlayer.seekTo(5000)

        // Assert
        verify { mockPlayerEngine.seekTo(5000) }
    }

    @Test
    fun shouldReturnCurrentPosition() {
        // Arrange
        val file = File("/path/to/audio.m4a")
        every { mockPlayerEngine.getCurrentPosition() } returns 3000
        audioPlayer.play(file)

        // Act
        val position = audioPlayer.getCurrentPosition()

        // Assert
        assertEquals(3000, position)
    }

    @Test
    fun shouldReturnDuration() {
        // Arrange
        val file = File("/path/to/audio.m4a")
        every { mockPlayerEngine.getDuration() } returns 10000
        audioPlayer.play(file)

        // Act
        val duration = audioPlayer.getDuration()

        // Assert
        assertEquals(10000, duration)
    }

    @Test
    fun shouldReturnIsPlayingState() {
        // Arrange
        val file = File("/path/to/audio.m4a")
        every { mockPlayerEngine.isPlaying() } returns true
        audioPlayer.play(file)

        // Act
        val isPlaying = audioPlayer.isPlaying()

        // Assert
        assertTrue(isPlaying)
    }

    @Test
    fun shouldReleaseResources() {
        // Act
        audioPlayer.release()

        // Assert
        verify { mockPlayerEngine.release() }
    }

    @Test
    fun shouldStopCurrentPlaybackBeforeStartingNew() {
        // Arrange
        val file1 = File("/path/to/audio1.m4a")
        val file2 = File("/path/to/audio2.m4a")
        audioPlayer.play(file1)

        // Act
        audioPlayer.play(file2)

        // Assert
        verify { mockPlayerEngine.stop() }
        verify { mockPlayerEngine.reset() }
        verify { mockPlayerEngine.setDataSource(file2.absolutePath) }
    }
}