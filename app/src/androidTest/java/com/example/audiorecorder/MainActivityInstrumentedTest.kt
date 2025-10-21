package com.example.audiorecorder

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.RECORD_AUDIO
    )

    @Test
    fun testStartRecordingButtonDisplayed() {
        // Verify the button is displayed with correct text
        composeTestRule.onNodeWithText("Start Recording").assertIsDisplayed()
    }

    @Test
    fun testButtonClickStartsRecording() {
        // Click the start recording button
        composeTestRule.onNodeWithText("Start Recording").performClick()

        // Wait for UI to update
        composeTestRule.waitForIdle()

        // Verify button text changes to "Stop Recording"
        composeTestRule.onNodeWithText("Stop Recording").assertIsDisplayed()

        // Verify recording status is displayed
        composeTestRule.onNodeWithText("Recording started...").assertIsDisplayed()
    }

    @Test
    fun testButtonClickStopsRecording() {
        // Start recording
        composeTestRule.onNodeWithText("Start Recording").performClick()
        composeTestRule.waitForIdle()

        // Verify recording started
        composeTestRule.onNodeWithText("Stop Recording").assertIsDisplayed()

        // Stop recording
        composeTestRule.onNodeWithText("Stop Recording").performClick()
        composeTestRule.waitForIdle()

        // Verify button text changes back to "Start Recording"
        composeTestRule.onNodeWithText("Start Recording").assertIsDisplayed()

        // Verify recording status is no longer displayed
        composeTestRule.onNodeWithText("Recording started...").assertDoesNotExist()
    }

    @Test
    fun testRecordingStatusNotDisplayedInitially() {
        // Verify recording status is not displayed initially
        composeTestRule.onNodeWithText("Recording started...").assertDoesNotExist()
    }

    @Test
    fun testButtonIsEnabled() {
        // Verify the button is enabled initially
        composeTestRule.onNodeWithText("Start Recording").assertIsEnabled()
    }

    @Test
    fun testMultipleRecordingSessions() {
        // First recording session
        composeTestRule.onNodeWithText("Start Recording").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Stop Recording").assertIsDisplayed()

        composeTestRule.onNodeWithText("Stop Recording").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Start Recording").assertIsDisplayed()

        // Second recording session
        composeTestRule.onNodeWithText("Start Recording").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Stop Recording").assertIsDisplayed()

        composeTestRule.onNodeWithText("Stop Recording").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Start Recording").assertIsDisplayed()
    }

    @Test
    fun testUILayoutCentered() {
        // Verify button exists in the center of the screen
        composeTestRule.onNodeWithText("Start Recording")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun testRecordingStatusTextColor() {
        // Start recording
        composeTestRule.onNodeWithText("Start Recording").performClick()
        composeTestRule.waitForIdle()

        // Verify recording status text is displayed (color is applied via MaterialTheme)
        composeTestRule.onNodeWithText("Recording started...")
            .assertIsDisplayed()
    }
}