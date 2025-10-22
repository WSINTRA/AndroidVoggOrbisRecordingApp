package com.example.audiorecorder.playerUtils

import android.media.AudioAttributes
import android.media.MediaPlayer

/**
 * Real implementation of PlayerEngine using Android's MediaPlayer
 */
class RealPlayerEngine : PlayerEngine {

    private val mediaPlayer = MediaPlayer().apply {
        // Set audio attributes to use media playback (loudspeaker)
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

    override fun setDataSource(path: String) {
        mediaPlayer.setDataSource(path)
    }

    override fun prepare() {
        mediaPlayer.prepare()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position.toLong(), MediaPlayer.SEEK_CLOSEST)
    }

    override fun getCurrentPosition(): Int {
        return try {
            mediaPlayer.currentPosition
        } catch (e: IllegalStateException) {
            println("Error getting current position: ${e.message}")
            0
        }
    }

    override fun getDuration(): Int {
        return try {
            mediaPlayer.duration
        } catch (e: IllegalStateException) {
            println("Error getting duration: ${e.message}")
            0
        }
    }

    override fun getAudioId(): Int {
        return try {
            mediaPlayer.audioSessionId
        } catch (e: IllegalStateException) {
            println("Error getting audio ID: ${e.message}")
            0
        }
    }

    override fun isPlaying(): Boolean {
        return try {
            mediaPlayer.isPlaying
        } catch (e: IllegalStateException) {
            println("Error checking if playing: ${e.message}")
            false
        }
    }

    override fun release() {
        try {
            mediaPlayer.release()
        } catch (e: IllegalStateException) {
            println("Error releasing media player: ${e.message}")
            // Ignore
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }
}