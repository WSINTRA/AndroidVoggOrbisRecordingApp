package com.example.audiorecorder.playerUtils

/**
 * Abstraction for audio playback engine
 */
interface PlayerEngine {
    fun setDataSource(path: String)
    fun prepare()
    fun start()
    fun pause()
    fun stop()
    fun reset()
    fun seekTo(position: Int)
    fun getCurrentPosition(): Int
    fun getDuration(): Int
    fun getAudioId(): Int
    fun isPlaying(): Boolean
    fun release()
    fun setOnCompletionListener(listener: () -> Unit)
}