package com.example.audiorecorder.ui.components

/**
 * Represents the state of a three-band equalizer
 *
 * @param lowGain Gain value for low frequencies (< 300 Hz), range 0-10
 * @param midGain Gain value for mid frequencies (300 Hz - 12 kHz), range 0-10
 * @param highGain Gain value for high frequencies (> 12 kHz), range 0-10
 */
data class ThreeBandEqState(
    val lowGain: Float,
    val midGain: Float,
    val highGain: Float
)