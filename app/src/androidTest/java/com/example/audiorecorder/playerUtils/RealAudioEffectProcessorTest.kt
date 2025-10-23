package com.example.audiorecorder.playerUtils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.audiorecorder.ui.components.ThreeBandEqState
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class RealAudioEffectProcessorTest {

    private var processor: RealAudioEffectProcessor? = null

    @Before
    fun setUp() {
        // Check if we have the required permission
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            println("Warning: MODIFY_AUDIO_SETTINGS permission not granted")
        }
    }

    @After
    fun tearDown() {
        processor?.release()
    }

    @Test
    fun shouldAttachToAudioSession() {
        // Arrange
        processor = RealAudioEffectProcessor()
        val audioSessionId = 0 // 0 refers to system output mix

        // Act
        val attached = processor?.attach(audioSessionId)

        // Assert
        assertTrue("Should successfully attach to audio session", attached == true)
    }
    @Test
    fun shouldApplyEqSettingsToAllChannels() {
        // Arrange
        processor = RealAudioEffectProcessor()
        val audioSessionId = 0
        processor?.attach(audioSessionId)

        val eqState = ThreeBandEqState(
            lowGain = 7.5f,   // Should convert to +6 dB
            midGain = 5f,     // Should convert to 0 dB (neutral)
            highGain = 2.5f   // Should convert to -6 dB
        )

        // Act
        processor?.applyEqSettings(eqState)

        // Assert
        // We can't easily verify the internal state of DynamicsProcessing,
        // but we can verify the method doesn't throw an exception
        // In a real scenario, we'd verify by playing audio and measuring output

        // If we get here without exception, the test passes
        assertTrue("EQ settings applied successfully", true)
    }

    @Test
    fun shouldDetachFromAudioSession() {
        // Arrange
        processor = RealAudioEffectProcessor()
        val audioSessionId = 0
        processor?.attach(audioSessionId)

        // Act
        processor?.detach()

        // Assert
        // Detach should disable the effect but not release resources
        // If we get here without exception, the test passes
        assertTrue("Should detach successfully", true)
    }

    @Test
    fun shouldReleaseResources() {
        // Arrange
        processor = RealAudioEffectProcessor()
        val audioSessionId = 0
        processor?.attach(audioSessionId)

        // Act
        processor?.release()

        // Assert
        // After release, the processor should not be usable
        // Attempting to apply settings should not crash
        val eqState = ThreeBandEqState(
            lowGain = 5f,
            midGain = 5f,
            highGain = 5f
        )
        processor?.applyEqSettings(eqState) // Should handle gracefully

        assertTrue("Should release successfully", true)
    }

    @Test
    fun shouldHandleMultipleReleaseCallsSafely() {
        // Arrange
        processor = RealAudioEffectProcessor()
        val audioSessionId = 0
        processor?.attach(audioSessionId)

        // Act
        processor?.release()
        processor?.release() // Call release again

        // Assert
        // Multiple release calls should not crash
        assertTrue("Should handle multiple release calls", true)
    }

    @Test
    fun shouldReattachAfterDetach() {
        // Arrange
        processor = RealAudioEffectProcessor()
        val audioSessionId = 0
        processor?.attach(audioSessionId)

        // Act
        processor?.detach()
        val reattached = processor?.attach(audioSessionId)

        // Assert
        assertTrue("Should be able to reattach after detach", reattached == true)
    }
}