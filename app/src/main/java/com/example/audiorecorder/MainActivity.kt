package com.example.audiorecorder

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.example.audiorecorder.ui.theme.AudioRecorderTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : ComponentActivity() {
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var recordedTakesRepository: RecordedTakesRepository
    private lateinit var audioPlayer: AudioPlayer

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, start recording
            handleStartRecording()
        } else {
            // Permission denied, show error
            MaterialAlertDialogBuilder(this).apply {
                setMessage("Microphone permission is required to record audio.")
                setPositiveButton("OK") { _, _ -> }
            }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize dependencies
        val engine: RecorderEngine = RealRecorderEngine(this)
        val outputFactory: OutputFileFactory = RealOutputFileFactory(this)
        audioRecorder = AudioRecorder(engine, outputFactory)

        // Initialize repository for managing recorded takes
        val fileProvider: FileProvider = RealFileProvider(this)
        val metadataReaderFactory: MediaMetadataRetrieverFactory = RealMediaMetadataRetrieverFactory()
        val audioMetadataReader: AudioMetadataReader = RealAudioMetadataReader(metadataReaderFactory)
        recordedTakesRepository = RecordedTakesRepository(fileProvider, audioMetadataReader)

        // Initialize audio player
        val playerEngine: PlayerEngine = RealPlayerEngine()
        audioPlayer = AudioPlayer(playerEngine)

        setContent {
            AudioRecorderTheme {
                MainScreenContainer()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.release()
    }

    @Composable
    private fun MainScreenContainer() {
        var isRecording by remember { mutableStateOf(false) }
        var recordedTakes by remember { mutableStateOf(recordedTakesRepository.getAllTakes()) }
        var currentlyPlayingTake by remember { mutableStateOf<RecordedTake?>(null) }

        MainScreen(
            recordedTakes = recordedTakes,
            isRecording = isRecording,
            currentlyPlayingTake = currentlyPlayingTake,
            isPlaying = audioPlayer.isPlaying(),
            onStartRecordingClick = {
                // Stop playback before recording
                if (currentlyPlayingTake != null) {
                    audioPlayer.stop()
                    currentlyPlayingTake = null
                }
                handleStartRecording()
                isRecording = true
            },
            onStopRecordingClick = {
                handleStopRecording()
                isRecording = false
                // Refresh the list after recording stops
                recordedTakes = recordedTakesRepository.getAllTakes()
            },
            onPlayClick = { take ->
                // Stop recording if active
                if (isRecording) {
                    handleStopRecording()
                    isRecording = false
                    recordedTakes = recordedTakesRepository.getAllTakes()
                }

                // Toggle playback
                if (currentlyPlayingTake == take && audioPlayer.isPlaying()) {
                    audioPlayer.pause()
                    currentlyPlayingTake = null
                } else {
                    audioPlayer.play(take.file)
                    currentlyPlayingTake = take

                    // Set completion listener to clear playing state
                    audioPlayer.setOnCompletionListener {
                        currentlyPlayingTake = null
                    }
                }
            },
            onDeleteClick = { take ->
                // Stop playback if this file is playing
                if (currentlyPlayingTake == take) {
                    audioPlayer.stop()
                    currentlyPlayingTake = null
                }

                // Delete the recording
                val deleted = recordedTakesRepository.deleteTake(take)
                if (deleted) {
                    // Refresh the list
                    recordedTakes = recordedTakesRepository.getAllTakes()
                }
            },
            onRequestPermission = {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        )
    }

    private fun handleStartRecording() {
        try {
            val fileName = audioRecorder.start()
            println("Recording started - $fileName")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleStopRecording() {
        try {
            audioRecorder.stop()
            println("Recording stopped")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}