package com.example.audiorecorder

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RealOutputFileFactory(private val context: Context) : OutputFileFactory {
    override fun nextFile(): File {
        val outputDir = context.filesDir
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val fileName = "recording_$timestamp.mp4"
        return File(outputDir, fileName)
    }
}