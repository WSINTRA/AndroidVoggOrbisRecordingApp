package com.example.audiorecorder.fileUtils

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Real implementation of OutputFileFactory
 */
class RealOutputFileFactory(
    private val fileProvider: FileProvider
) : OutputFileFactory {

    override fun nextFile(): File {
        val outputDirectory = fileProvider.getRecordingsDirectory()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val fileName = "recording_$timestamp.m4a"
        val file = File(outputDirectory, fileName)
        println("Creating output file: ${file.absolutePath}")
        return file
    }
}