package com.example.audiorecorder.playerUtils

import com.example.audiorecorder.ui.components.ThreeBandEqState

/**
 * Abstraction for audio effect processing during playback
 *
 * Manages DynamicsProcessing effect lifecycle and EQ band adjustments.
 * Effects are applied globally to all audio playback.
 *
 * TODO: Future enhancement - persist EQ settings across app restarts
 */
interface AudioEffectProcessor {
    /**
     * Attaches the audio effect to an audio session
     *
     * @param audioSessionId The audio session ID from PlayerEngine.getAudioId()
     * @return true if attachment was successful, false otherwise
     */
    fun attach(audioSessionId: Int): Boolean

    /**
     * Applies EQ settings from UI state to the audio effect
     * Converts UI gain values (0-10) to appropriate dB values for each frequency band:
     * - Low: < 300 Hz
     * - Mid: 300 Hz - 12 kHz
     * - High: > 12 kHz
     *
     * @param eqState Current EQ state from UI sliders
     */
    fun applyEqSettings(eqState: ThreeBandEqState)

    /**
     * Detaches the audio effect from the current audio session
     */
    fun detach()

    /**
     * Releases all resources held by the audio effect processor
     * Should be called when the processor is no longer needed
     */
    fun release()
}