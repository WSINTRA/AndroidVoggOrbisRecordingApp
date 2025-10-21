package com.example.audiorecorder

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class RecordedTakeItemInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayFilename() {
        // Arrange
        val take = createTestTake(filename = "recording_001.m4a")

        // Act
        composeTestRule.setContent {
            RecordedTakeItem(
                take = take,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("recording_001.m4a").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayFormattedDuration() {
        // Arrange
        val take = createTestTake(duration = 125000L) // 2:05

        // Act
        composeTestRule.setContent {
            RecordedTakeItem(
                take = take,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("2:05").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayFormattedDate() {
        // Arrange
        val date = Date(1234567890000L) // Feb 13, 2009
        val take = createTestTake(dateRecorded = date)

        // Act
        composeTestRule.setContent {
            RecordedTakeItem(
                take = take,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert
        val expectedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
    }

    @Test
    fun shouldHavePlayButton() {
        // Arrange
        val take = createTestTake()

        // Act
        composeTestRule.setContent {
            RecordedTakeItem(
                take = take,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithContentDescription("Play").assertIsDisplayed()
    }

    @Test
    fun shouldHaveDeleteButton() {
        // Arrange
        val take = createTestTake()

        // Act
        composeTestRule.setContent {
            RecordedTakeItem(
                take = take,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
    }

    @Test
    fun shouldCallOnPlayClickWhenPlayButtonClicked() {
        // Arrange
        val take = createTestTake()
        var playClicked = false

        // Act
        composeTestRule.setContent {
            RecordedTakeItem(
                take = take,
                onPlayClick = { playClicked = true },
                onDeleteClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Play").performClick()
        composeTestRule.waitForIdle()

        // Assert
        assert(playClicked)
    }

    @Test
    fun shouldCallOnDeleteClickWhenDeleteButtonClicked() {
        // Arrange
        val take = createTestTake()
        var deleteClicked = false

        // Act
        composeTestRule.setContent {
            RecordedTakeItem(
                take = take,
                onPlayClick = {},
                onDeleteClick = { deleteClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        composeTestRule.waitForIdle()

        // Assert
        assert(deleteClicked)
    }

    private fun createTestTake(
        filename: String = "test.m4a",
        duration: Long = 5000L,
        dateRecorded: Date = Date()
    ): RecordedTake {
        return RecordedTake(
            file = File("/test/$filename"),
            duration = duration,
            dateRecorded = dateRecorded
        )
    }
}