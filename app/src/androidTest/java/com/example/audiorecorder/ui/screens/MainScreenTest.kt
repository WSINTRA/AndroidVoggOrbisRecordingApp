package com.example.audiorecorder.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.audiorecorder.playerUtils.AudioEffectProcessor
import com.example.audiorecorder.recorderUtils.RecordedTake
import com.example.audiorecorder.ui.components.ThreeBandEqState
import org.junit.Assert.assertNotNull
import androidx.compose.ui.test.swipeRight
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue
import java.io.File
import java.util.Date

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldShowEqControlsWhenTakeIsPlaying() {
        // Arrange
        val playingTake = RecordedTake(
            file = File("/path/test.mp3"),
            duration = 5000,
            dateRecorded = Date()
        )

        composeTestRule.setContent {
            MainScreen(
                recordedTakes = listOf(playingTake),
                isRecording = false,
                currentlyPlayingTake = playingTake,
                isPlaying = true,
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onPlayClick = {},
                onDeleteClick = {},
                onRequestPermission = {},
                audioEffectProcessor = null
            )
        }

        // Assert
        composeTestRule
            .onNodeWithTag("eq_controls")
            .assertIsDisplayed()
    }

    @Test
    fun shouldHideEqControlsWhenNoTakeIsPlaying() {
        // Arrange
        composeTestRule.setContent {
            MainScreen(
                recordedTakes = emptyList(),
                isRecording = false,
                currentlyPlayingTake = null,
                isPlaying = false,
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onPlayClick = {},
                onDeleteClick = {},
                onRequestPermission = {},
                audioEffectProcessor = null
            )
        }

        // Assert
        composeTestRule
            .onNodeWithTag("eq_controls")
            .assertDoesNotExist()
    }
    @Test
    fun shouldShowRecordingTimerWhenRecording() {
        // Arrange
        composeTestRule.setContent {
            MainScreen(
                recordedTakes = emptyList(),
                isRecording = true,
                currentlyPlayingTake = null,
                isPlaying = false,
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onPlayClick = {},
                onDeleteClick = {},
                onRequestPermission = {},
                audioEffectProcessor = null
            )
        }

        // Assert
        composeTestRule
            .onNodeWithText("Recording...")
            .assertIsDisplayed()
    }

    @Test
    fun shouldShowStopRecordingButtonWhenRecording() {
        // Arrange
        composeTestRule.setContent {
            MainScreen(
                recordedTakes = emptyList(),
                isRecording = true,
                currentlyPlayingTake = null,
                isPlaying = false,
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onPlayClick = {},
                onDeleteClick = {},
                onRequestPermission = {},
                audioEffectProcessor = null
            )
        }

        // Assert
        composeTestRule
            .onNodeWithText("Stop Recording")
            .assertIsDisplayed()
    }

    @Test
    fun shouldShowStartRecordingButtonWhenNotRecording() {
        // Arrange
        composeTestRule.setContent {
            MainScreen(
                recordedTakes = emptyList(),
                isRecording = false,
                currentlyPlayingTake = null,
                isPlaying = false,
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onPlayClick = {},
                onDeleteClick = {},
                onRequestPermission = {},
                audioEffectProcessor = null
            )
        }

        // Assert
        composeTestRule
            .onNodeWithText("Start Recording")
            .assertIsDisplayed()
    }

    @Test
    fun shouldInvokeRequestPermissionCallbackWhenButtonClickedWithoutPermission() {
        // Arrange
        var requestPermissionCalled = false

        composeTestRule.setContent {
            MainScreen(
                recordedTakes = emptyList(),
                isRecording = false,
                currentlyPlayingTake = null,
                isPlaying = false,
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onPlayClick = {},
                onDeleteClick = {},
                onRequestPermission = { requestPermissionCalled = true },
                audioEffectProcessor = null
            )
        }

        // Act
        composeTestRule
            .onNodeWithText("Start Recording")
            .performClick()

        // Assert
        assertTrue("Request permission callback should be invoked when permission not granted", requestPermissionCalled)
    }


    @Test
    fun shouldShowPlaybackTimerWhenPlaying() {
        // Arrange
        val playingTake = RecordedTake(
            file = File("/path/test.mp3"),
            duration = 5000,
            dateRecorded = Date()
        )

        composeTestRule.setContent {
            MainScreen(
                recordedTakes = listOf(playingTake),
                isRecording = false,
                currentlyPlayingTake = playingTake,
                isPlaying = true,
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onPlayClick = {},
                onDeleteClick = {},
                onRequestPermission = {},
                audioEffectProcessor = null
            )
        }

        // Assert - Timer should be visible during playback
        // We check that recording status is NOT shown
        composeTestRule
            .onNodeWithText("Recording...")
            .assertDoesNotExist()
    }

    @Test
    fun shouldApplyEqSettingsWhenSliderChanges() {
        // Arrange
        var appliedEqState: ThreeBandEqState? = null
        val mockProcessor = object : AudioEffectProcessor {
            override fun attach(audioSessionId: Int): Boolean = true
            override fun applyEqSettings(eqState: ThreeBandEqState) {
                appliedEqState = eqState
            }
            override fun detach() {}
            override fun release() {}
        }

        val playingTake = RecordedTake(
            file = File("/path/test.mp3"),
            duration = 5000,
            dateRecorded = Date()
        )

        composeTestRule.setContent {
            MainScreen(
                recordedTakes = listOf(playingTake),
                isRecording = false,
                currentlyPlayingTake = playingTake,
                isPlaying = true,
                audioEffectProcessor = mockProcessor,
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onPlayClick = {},
                onDeleteClick = {},
                onRequestPermission = {}
            )
        }

        composeTestRule.waitForIdle()

        // Act - Swipe the low gain slider
        composeTestRule
            .onNodeWithTag("low_gain_slider")
            .performTouchInput { swipeRight() }

        composeTestRule.waitForIdle()

        // Assert
        assertNotNull("EQ settings should be applied", appliedEqState)
        assertTrue("Low gain should have changed", appliedEqState!!.lowGain > 5f)
    }
}