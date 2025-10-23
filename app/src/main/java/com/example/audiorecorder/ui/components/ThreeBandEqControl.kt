package com.example.audiorecorder.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import kotlin.math.roundToInt

/**
 * A three-band equalizer control with sliders for low, mid, and high frequency gains
 *
 * @param state Current state of the EQ with gain values (0-10 range)
 * @param onStateChange Callback invoked when any gain value changes
 */
@Composable
fun ThreeBandEqControl(
    state: ThreeBandEqState,
    onStateChange: (ThreeBandEqState) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        Text("Three Band Equalizer")

        // Low frequency slider (< 300 Hz)
        Slider(
            value = state.lowGain,
            enabled = enabled,
            onValueChange = { newValue ->
                onStateChange(
                    state.copy(lowGain = newValue)
                )
            },
            valueRange = 0f..10f,
            modifier = Modifier.testTag("low_gain_slider")
        )
        Text("Low Gain: ${state.lowGain.roundToInt()}")


        // Mid frequency slider (300 Hz - 12 kHz)
        Slider(
            value = state.midGain,
            enabled = enabled,
            onValueChange = { newValue ->
                onStateChange(
                    state.copy(midGain = newValue)
                )
            },
            valueRange = 0f..10f,
            modifier = Modifier.testTag("mid_gain_slider")
        )
        Text("Mid Gain: ${state.midGain.roundToInt()}")


        // High frequency slider (> 12 kHz)

        Slider(
            value = state.highGain,
            enabled = enabled,
            onValueChange = { newValue ->
                onStateChange(
                    state.copy(highGain = newValue)
                )
            },
            valueRange = 0f..10f,
            modifier = Modifier.testTag("high_gain_slider")
        )
        Text("High Gain: ${state.highGain.roundToInt()}")

    }
}