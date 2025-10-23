package com.example.audiorecorder.playerUtils

import org.junit.Test
import org.junit.Assert.*

class AudioEffectProcessorTest {

    @Test
    fun shouldConvertUiGainToDecibelRange() {
        // Arrange
        val processor = RealAudioEffectProcessor()

        // Act & Assert
        // UI range: 0-10, where 5 is neutral (0 dB)
        // Expected dB range: -12 dB to +12 dB

        // Test neutral position
        assertEquals(0f, processor.convertGainToDecibels(5f), 0.01f)

        // Test minimum gain
        assertEquals(-12f, processor.convertGainToDecibels(0f), 0.01f)

        // Test maximum gain
        assertEquals(12f, processor.convertGainToDecibels(10f), 0.01f)

        // Test mid-range values
        assertEquals(-6f, processor.convertGainToDecibels(2.5f), 0.01f)
        assertEquals(6f, processor.convertGainToDecibels(7.5f), 0.01f)
    }
}