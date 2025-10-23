package com.example.audiorecorder.ui.components

import org.junit.Test
import org.junit.Assert.*

class ThreeBandEqStateTest {

    @Test
    fun shouldCreateStateWithThreeGainValues() {
        // Arrange & Act
        val state = ThreeBandEqState(
            lowGain = 5f,
            midGain = 5f,
            highGain = 5f
        )

        // Assert
        assertEquals(5f, state.lowGain, 0.01f)
        assertEquals(5f, state.midGain, 0.01f)
        assertEquals(5f, state.highGain, 0.01f)
    }
}