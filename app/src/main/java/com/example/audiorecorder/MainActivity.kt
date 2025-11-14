package com.example.audiorecorder

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.example.audiorecorder.fileUtils.AudioMetadataReader
import com.example.audiorecorder.fileUtils.ExportResult
import com.example.audiorecorder.fileUtils.FileExporter
import com.example.audiorecorder.fileUtils.FileProvider
import com.example.audiorecorder.fileUtils.MediaMetadataRetrieverFactory
import com.example.audiorecorder.fileUtils.OutputFileFactory
import com.example.audiorecorder.fileUtils.RealAudioMetadataReader
import com.example.audiorecorder.fileUtils.RealFileExporter
import com.example.audiorecorder.fileUtils.RealFileProvider
import com.example.audiorecorder.fileUtils.RealMediaMetadataRetrieverFactory
import com.example.audiorecorder.fileUtils.RealOutputFileFactory
import com.example.audiorecorder.playerUtils.AudioPlayer
import com.example.audiorecorder.playerUtils.PlayerEngine
import com.example.audiorecorder.recorderUtils.AudioRecorder
import com.example.audiorecorder.recorderUtils.RealRecorderEngine
import com.example.audiorecorder.recorderUtils.RecordedTake
import com.example.audiorecorder.fileUtils.RecordedTakesRepository
import com.example.audiorecorder.playerUtils.AudioEffectProcessor
import com.example.audiorecorder.playerUtils.RealAudioEffectProcessor
import com.example.audiorecorder.playerUtils.RealAudioTrackPlayerEngine
import com.example.audiorecorder.recorderUtils.RecorderEngine
import com.example.audiorecorder.ui.screens.MainScreen
import com.example.audiorecorder.ui.theme.AudioRecorderTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var recordedTakesRepository: RecordedTakesRepository
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var fileProvider: FileProvider
    private lateinit var fileExporter: FileExporter
    private lateinit var audioEffectProcessor: AudioEffectProcessor

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

        fileProvider = RealFileProvider(this)
        // Initialize dependencies
        val engine: RecorderEngine = RealRecorderEngine(this)

        val outputFactory: OutputFileFactory = RealOutputFileFactory(fileProvider)
        audioRecorder = AudioRecorder(engine, outputFactory)
// Initialize file exporter
        fileExporter = RealFileExporter(this)
        // Initialize repository for managing recorded takes
        fileProvider = RealFileProvider(this)  // Store reference
        val metadataReaderFactory: MediaMetadataRetrieverFactory =
            RealMediaMetadataRetrieverFactory()
        val audioMetadataReader: AudioMetadataReader =
            RealAudioMetadataReader(metadataReaderFactory)
        recordedTakesRepository = RecordedTakesRepository(fileProvider, audioMetadataReader)

        // Initialize audio player
        val playerEngine: PlayerEngine = RealAudioTrackPlayerEngine(
            context = this,
            bufferSizeInFrames = 4096  // ~85ms at 48kHz - balanced latency/stability
        )
        audioPlayer = AudioPlayer(playerEngine)

        // Initialize audio effect processor
        audioEffectProcessor = RealAudioEffectProcessor()


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

        // Attach/detach audio effect based on playback state
        LaunchedEffect(audioPlayer.isPlaying()) {
            if (audioPlayer.isPlaying()) {
                // Attach effect to current audio session
                val audioSessionId = audioPlayer.getAudioSessionId()
                audioEffectProcessor.attach(audioSessionId)
            } else {
                // Detach effect when not playing
                audioEffectProcessor.detach()
            }
        }

        MainScreen(
            recordedTakes = recordedTakes,
            isRecording = isRecording,
            currentlyPlayingTake = currentlyPlayingTake,
            isPlaying = audioPlayer.isPlaying(),
            audioEffectProcessor = audioEffectProcessor,
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
            onExportClick = { take ->
                handleExport(take)
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
            println("=== START RECORDING ===")
            val file = audioRecorder.start()
            println("Recording started - File: ${file.absolutePath}")
            println("File exists after start: ${file.exists()}")
            println("Parent directory: ${file.parentFile?.absolutePath}")
            println("Parent exists: ${file.parentFile?.exists()}")
        } catch (e: Exception) {
            println("ERROR starting recording: ${e.message}")
            e.printStackTrace()
        }
    }
    private fun handleExport(take: RecordedTake) {
        // Run export on background thread
        CoroutineScope(Dispatchers.IO).launch {
            // Use filename without extension as display name
            val displayName = take.file.nameWithoutExtension

            val result = fileExporter.exportToMusic(take.file, displayName)

            // Show result on main thread (silent for now as per requirements)
            withContext(Dispatchers.Main) {
                when (result) {
                    is ExportResult.Success -> {
                        // Success - no UI feedback for first iteration
                        println("Export successful: ${result.uri}")
                    }
                    is ExportResult.Error -> {
                        // Error - no UI feedback for first iteration
                        println("Export failed: ${result.message}")
                    }
                }
            }
        }
    }
    private fun handleStopRecording() {
        try {
            println("=== STOP RECORDING ===")
            val file = audioRecorder.stop()
            println("Recording stopped - File: ${file.absolutePath}")
            println("File exists after stop: ${file.exists()}")
            println("File size: ${file.length()} bytes")

            // Debug: Check recordings directory
            val recordingsDir = (fileProvider as RealFileProvider).getRecordingsDirectory()
            println("Recordings directory: ${recordingsDir.absolutePath}")
            println("Directory exists: ${recordingsDir.exists()}")
            val files = recordingsDir.listFiles()
            println("Files in directory: ${files?.size ?: 0}")
            files?.forEach { f ->
                println("  - ${f.name} (${f.length()} bytes)")
            }

            // Debug: Check what repository finds
            val allTakes = recordedTakesRepository.getAllTakes()
            println("Total recordings found by repository: ${allTakes.size}")
            allTakes.forEach { take ->
                println("  Recording: ${take.filename}, exists: ${take.file.exists()}, size: ${take.file.length()}")
            }
        } catch (e: Exception) {
            println("ERROR stopping recording: ${e.message}")
            e.printStackTrace()
        }
    }
}