package com.example.audiorecorder.playerUtils

import java.io.File

/**
 * Manages audio playback
 */
class AudioPlayer(private val playerEngine: PlayerEngine) {

    private var currentFile: File? = null

    /**
     * Start playing an audio file
     */
    fun play(file: File) {
        // Stop current playback if any
        if (currentFile != null) {
            stop()
        }

        currentFile = file
        playerEngine.setDataSource(file.absolutePath)
        playerEngine.prepare()
        playerEngine.start()
    }

    /**
     * Pause playback
     */
    fun pause() {
        playerEngine.pause()
    }

    /**
     * Resume playback
     */
    fun resume() {
        playerEngine.start()
    }

    /**
     * Stop playback and release resources
     */
    fun stop() {
        playerEngine.stop()
        playerEngine.reset()
        currentFile = null
    }

    /**
     * Seek to a specific position in milliseconds
     */
    fun seekTo(position: Int) {
        playerEngine.seekTo(position)
    }

    /**
     * Get current playback position in milliseconds
     */
    fun getCurrentPosition(): Int {
        return playerEngine.getCurrentPosition()
    }

    /**
     * Get duration of current audio in milliseconds
     */
    fun getDuration(): Int {
        return playerEngine.getDuration()
    }

    /**
     * Get the audio session ID
     */
    fun getAudioSessionId(): Int {
        return playerEngine.getAudioId()
    }

    /**
     * Check if audio is currently playing
     */
    fun isPlaying(): Boolean {
        return playerEngine.isPlaying()
    }

    /**
     * Release all resources
     */
    fun release() {
        stop()
        playerEngine.release()
    }

    /**
     * Set a listener for when playback completes
     */
    fun setOnCompletionListener(listener: () -> Unit) {
        playerEngine.setOnCompletionListener(listener)
    }
}