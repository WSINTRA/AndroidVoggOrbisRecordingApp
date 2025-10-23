package com.example.audiorecorder.playerUtils

import android.media.audiofx.DynamicsProcessing
import com.example.audiorecorder.ui.components.ThreeBandEqState

/**
 * Implementation of AudioEffectProcessor using Android's DynamicsProcessing
 *
 * Applies 3-band EQ settings to audio playback globally.
 * Requires API level 28+
 */
class RealAudioEffectProcessor : AudioEffectProcessor {

    private var dynamicsProcessing: DynamicsProcessing? = null

    /**
     * Converts UI gain value (0-10 range) to decibels (-12 dB to +12 dB)
     *
     * @param gain UI gain value where 5 is neutral (0 dB)
     * @return gain value in decibels
     */
    fun convertGainToDecibels(gain: Float): Float {
        // UI range: 0-10, center at 5
        // dB range: -12 to +12, center at 0
        // Formula: (gain - 5) * 2.4 = dB
        // 0 -> -12, 5 -> 0, 10 -> +12
        return (gain - 5f) * 2.4f
    }

    override fun attach(audioSessionId: Int): Boolean {
        return try {
            // Release any existing instance
            release()

            // Create config with 3-band PreEQ
            // Frequency bands: Low (< 300 Hz), Mid (300 Hz - 12 kHz), High (> 12 kHz)
            val config = DynamicsProcessing.Config.Builder(
                DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                2, // channelCount (stereo)
                true, // preEqInUse
                3, // preEqBandCount (low, mid, high)
                true, // mbcInUse
                0, // mbcBandCount (not using multi-band compression for now)
                true, // postEqInUse
                0, // postEqBandCount (not using post-EQ for now)
                true  // limiterInUse
            ).build()

            // Create DynamicsProcessing instance
            dynamicsProcessing = DynamicsProcessing(0, audioSessionId, config)
            dynamicsProcessing?.enabled = true

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun applyEqSettings(eqState: ThreeBandEqState) {
        val dp = dynamicsProcessing ?: return

        try {
            // Convert UI gain values (0-10) to decibels (-12 to +12)
            val lowGainDb = convertGainToDecibels(eqState.lowGain)
            val midGainDb = convertGainToDecibels(eqState.midGain)
            val highGainDb = convertGainToDecibels(eqState.highGain)

            // Get the number of channels (typically 2 for stereo)
            val channelCount = dp.channelCount

            // Apply EQ settings to all channels
            for (channelIndex in 0 until channelCount) {
                // Get the PreEQ for this channel
                val preEq = dp.getPreEqByChannelIndex(channelIndex)

                // Band 0: Low frequencies (20 Hz - 250 Hz)
// Controls: Bass, kick drum, bass guitar fundamentals
                val lowBand = preEq.getBand(0)
                lowBand.isEnabled = true
                lowBand.cutoffFrequency = 250f
                lowBand.gain = lowGainDb
                dp.setPreEqBandByChannelIndex(channelIndex, 0, lowBand)

// Band 1: Mid frequencies (250 Hz - 4 kHz)
// Controls: Vocals, most instruments, presence
                val midBand = preEq.getBand(1)
                midBand.isEnabled = true
                midBand.cutoffFrequency = 4000f
                midBand.gain = midGainDb
                dp.setPreEqBandByChannelIndex(channelIndex, 1, midBand)

// Band 2: High frequencies (4 kHz - 20 kHz)
// Controls: Brilliance, air, cymbals, sibilance
                val highBand = preEq.getBand(2)
                highBand.isEnabled = true
                highBand.cutoffFrequency = 12000f  // Center of high range
                highBand.gain = highGainDb
                dp.setPreEqBandByChannelIndex(channelIndex, 2, highBand)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun detach() {
        dynamicsProcessing?.enabled = false
    }

    override fun release() {
        dynamicsProcessing?.release()
        dynamicsProcessing = null
    }
}