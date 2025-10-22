package com.example.audiorecorder

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.audiorecorder.ui.components.TimerUI
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerUIInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayInitialTime() {
        // Arrange & Act
        composeTestRule.setContent {
            TimerUI(elapsedTimeMillis = 0)
        }

        // Assert
        composeTestRule.onNodeWithText("00:00").assertIsDisplayed()
    }

    @Test
    fun shouldDisplaySeconds() {
        // Arrange & Act
        composeTestRule.setContent {
            TimerUI(elapsedTimeMillis = 5000) // 5 seconds
        }

        // Assert
        composeTestRule.onNodeWithText("00:05").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayMinutesAndSeconds() {
        // Arrange & Act
        composeTestRule.setContent {
            TimerUI(elapsedTimeMillis = 125000) // 2 minutes 5 seconds
        }

        // Assert
        composeTestRule.onNodeWithText("02:05").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayTensOfMinutes() {
        // Arrange & Act
        composeTestRule.setContent {
            TimerUI(elapsedTimeMillis = 725000) // 12 minutes 5 seconds
        }

        // Assert
        composeTestRule.onNodeWithText("12:05").assertIsDisplayed()
    }

    @Test
    fun shouldPadSecondsWithZero() {
        // Arrange & Act
        composeTestRule.setContent {
            TimerUI(elapsedTimeMillis = 63000) // 1 minute 3 seconds
        }

        // Assert
        composeTestRule.onNodeWithText("01:03").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayHoursIfNeeded() {
        // Arrange & Act
        composeTestRule.setContent {
            TimerUI(elapsedTimeMillis = 3665000) // 1 hour 1 minute 5 seconds
        }

        // Assert
        composeTestRule.onNodeWithText("01:01:05").assertIsDisplayed()
    }
}