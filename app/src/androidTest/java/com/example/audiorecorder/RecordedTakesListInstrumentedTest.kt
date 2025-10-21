package com.example.audiorecorder

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*

@RunWith(AndroidJUnit4::class)
class RecordedTakesListInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayEmptyStateWhenNoRecordings() {
        // Arrange
        val emptyList = emptyList<RecordedTake>()

        // Act
        composeTestRule.setContent {
            RecordedTakesList(
                takes = emptyList,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("No recordings yet").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayListOfRecordings() {
        // Arrange
        val takes = listOf(
            createTestTake(filename = "recording1.m4a"),
            createTestTake(filename = "recording2.m4a"),
            createTestTake(filename = "recording3.m4a")
        )

        // Act
        composeTestRule.setContent {
            RecordedTakesList(
                takes = takes,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("recording1.m4a").assertIsDisplayed()
        composeTestRule.onNodeWithText("recording2.m4a").assertIsDisplayed()
        composeTestRule.onNodeWithText("recording3.m4a").assertIsDisplayed()
    }

    @Test
    fun shouldNotDisplayEmptyStateWhenRecordingsExist() {
        // Arrange
        val takes = listOf(createTestTake())

        // Act
        composeTestRule.setContent {
            RecordedTakesList(
                takes = takes,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("No recordings yet").assertDoesNotExist()
    }

    @Test
    fun shouldCallOnPlayClickWhenPlayButtonClicked() {
        // Arrange
        val take = createTestTake(filename = "test.m4a")
        var clickedTake: RecordedTake? = null

        // Act
        composeTestRule.setContent {
            RecordedTakesList(
                takes = listOf(take),
                onPlayClick = { clickedTake = it },
                onDeleteClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Play").performClick()
        composeTestRule.waitForIdle()

        // Assert
        assert(clickedTake == take)
    }

    @Test
    fun shouldCallOnDeleteClickWhenDeleteButtonClicked() {
        // Arrange
        val take = createTestTake(filename = "test.m4a")
        var clickedTake: RecordedTake? = null

        // Act
        composeTestRule.setContent {
            RecordedTakesList(
                takes = listOf(take),
                onPlayClick = {},
                onDeleteClick = { clickedTake = it }
            )
        }

        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        composeTestRule.waitForIdle()

        // Assert
        assert(clickedTake == take)
    }

    @Test
    fun shouldScrollThroughLongList() {
        // Arrange
        val takes = (1..20).map {
            createTestTake(filename = "recording$it.m4a")
        }

        // Act
        composeTestRule.setContent {
            RecordedTakesList(
                takes = takes,
                onPlayClick = {},
                onDeleteClick = {}
            )
        }

        // Assert - first item visible
        composeTestRule.onNodeWithText("recording1.m4a").assertIsDisplayed()

        // Last item not visible initially
        composeTestRule.onNodeWithText("recording20.m4a").assertDoesNotExist()
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