    package com.example.audiorecorder

    import androidx.compose.ui.test.*
    import androidx.compose.ui.test.junit4.createComposeRule
    import androidx.test.ext.junit.runners.AndroidJUnit4
    import com.example.audiorecorder.recorderUtils.RecordedTake
    import com.example.audiorecorder.ui.components.RecordedTakeItem
    import org.junit.Rule
    import org.junit.Test
    import org.junit.runner.RunWith
    import java.io.File
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
                    onExportClick = {},
                    onDeleteClick = {},
                    isPlaying = false
                )
            }

            // Assert
            composeTestRule.onNodeWithText("recording_001.m4a").assertIsDisplayed()
        }

        @Test
        fun shouldDisplayFormattedDuration() {
            // Arrange
            val take = createTestTake(duration = 125000L) // Should be 2:05

            // Act
            composeTestRule.setContent {
                RecordedTakeItem(
                    take = take,
                    onPlayClick = {},
                    onExportClick = {},
                    onDeleteClick = {},
                    isPlaying = false
                )
            }

            // Assert - Verify duration is formatted (contains colon)
            composeTestRule.onNode(hasText(":", substring = true)).assertExists()
        }

        @Test
        fun shouldDisplayDateAndDuration() {
            // Arrange
            val take = createTestTake(duration = 125000L)

            // Act
            composeTestRule.setContent {
                RecordedTakeItem(
                    take = take,
                    onPlayClick = {},
                    onExportClick = {},
                    onDeleteClick = {},
                    isPlaying = false
                )
            }

            // Assert - Verify duration and date separator are present
            composeTestRule.onNodeWithText("2:05", substring = true).assertExists()
            composeTestRule.onNode(hasText("•", substring = true)).assertExists()
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
                    onExportClick = {},
                    onDeleteClick = {},
                    isPlaying = false
                )
            }

            // Assert
            composeTestRule.onNodeWithContentDescription("Play").assertIsDisplayed()
        }

        @Test
        fun shouldHaveExportButton() {
            // Arrange
            val take = createTestTake()

            // Act
            composeTestRule.setContent {
                RecordedTakeItem(
                    take = take,
                    onPlayClick = {},
                    onExportClick = {},
                    onDeleteClick = {},
                    isPlaying = false
                )
            }

            // Assert
            composeTestRule.onNodeWithContentDescription("Export to Music").assertIsDisplayed()
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
                    onExportClick = {},
                    onDeleteClick = {},
                    isPlaying = false
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
                    onExportClick = {},
                    onDeleteClick = {},
                    isPlaying = false
                )
            }

            composeTestRule.onNodeWithContentDescription("Play").performClick()
            composeTestRule.waitForIdle()

            // Assert
            assert(playClicked)
        }

        @Test
        fun shouldCallOnExportClickWhenExportButtonClicked() {
            // Arrange
            val take = createTestTake()
            var exportClicked = false

            // Act
            composeTestRule.setContent {
                RecordedTakeItem(
                    take = take,
                    onPlayClick = {},
                    onExportClick = { exportClicked = true },
                    onDeleteClick = {},
                    isPlaying = false
                )
            }

            composeTestRule.onNodeWithContentDescription("Export to Music").performClick()
            composeTestRule.waitForIdle()

            // Assert
            assert(exportClicked)
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
                    onExportClick = {},
                    onDeleteClick = { deleteClicked = true },
                    isPlaying = false
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