package com.example.audiorecorder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.audiorecorder.ui.theme.AudioRecorderTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : ComponentActivity() {
    private lateinit var audioRecorder: AudioRecorder
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, start recording
            startRecording()
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
        val engine: RecorderEngine = RealRecorderEngine(this)
        val outputFactory: OutputFileFactory = RealOutputFileFactory(this)
        audioRecorder = AudioRecorder(engine, outputFactory)

        setContent {
            AudioRecorderTheme {
                val context = LocalContext.current
                var isRecording by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Check permission status
                    val permissionGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    // Start/Stop Button
                    Button(
                        onClick = {
                            if (!isRecording) {
                                if (permissionGranted) {
                                    startRecording()
                                    isRecording = true
                                } else {
                                    requestPermissionLauncher.launch(
                                        Manifest.permission.RECORD_AUDIO
                                    )
                                }
                            } else {
                                stopRecording()
                                isRecording = false
                            }
                        },

                        enabled = !isRecording || permissionGranted
                    ) {
                        Text(if (isRecording) "Stop Recording" else "Start Recording")
                    }

                    // Status Text
                    if (isRecording) {
                        Text(
                            text = "Recording started...",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    private fun startRecording() {
        try {
            val fileName = audioRecorder.start()
            println("Recording started - $fileName")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        try {
            audioRecorder.stop()
            println("Recording stopped")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}