package com.example.audiorecorder.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ThreeBandEqControlTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldUpdateLowGainWhenLowKnobIsAdjusted() {
        // Arrange
        var capturedLowGain = 0f
        val initialState = ThreeBandEqState(
            lowGain = 5f,
            midGain = 5f,
            highGain = 5f
        )

        composeTestRule.setContent {
            ThreeBandEqControl(
                state = initialState,
                onStateChange = { newState ->
                    capturedLowGain = newState.lowGain
                }
            )
        }

        // Act
        composeTestRule
            .onNodeWithTag("low_gain_slider")
            .performTouchInput { swipeRight() }

        // Assert
        assertTrue("Low gain should increase from initial value of 5f", capturedLowGain > 5f)
    }

    @Test
    fun shouldUpdateMidGainWhenMidKnobIsAdjusted() {
        // Arrange
        var capturedMidGain = 0f
        val initialState = ThreeBandEqState(
            lowGain = 5f,
            midGain = 5f,
            highGain = 5f
        )

        composeTestRule.setContent {
            ThreeBandEqControl(
                state = initialState,
                onStateChange = { newState ->
                    capturedMidGain = newState.midGain
                }
            )
        }

        // Act
        composeTestRule
            .onNodeWithTag("mid_gain_slider")
            .performTouchInput { swipeRight() }

        // Assert
        assertTrue("Mid gain should increase from initial value of 5f", capturedMidGain > 5f)
    }
    @Test
    fun shouldUpdateHighGainWhenHighKnobIsAdjusted() {
        // Arrange
        var capturedHighGain = 0f
        val initialState = ThreeBandEqState(
            lowGain = 5f,
            midGain = 5f,
            highGain = 5f
        )

        composeTestRule.setContent {
            ThreeBandEqControl(
                state = initialState,
                onStateChange = { newState ->
                    capturedHighGain = newState.highGain
                }
            )
        }

        // Act
        composeTestRule
            .onNodeWithTag("high_gain_slider")
            .performTouchInput { swipeRight() }

        // Assert
        assertTrue("High gain should increase from initial value of 5f", capturedHighGain > 5f)
    }
}