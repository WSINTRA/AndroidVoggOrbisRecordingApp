package com.example.audiorecorder.recorderUtils

import com.example.audiorecorder.fileUtils.OutputFileFactory
import java.io.File

class AudioRecorder(
    private val engine: RecorderEngine,
    private val outputFactory: OutputFileFactory
) {
    private var currentFile: File? = null

    fun start(): File {
        val file = outputFactory.nextFile()
        currentFile = file
        engine.prepare(file)
        engine.start()
        return file
    }

    fun stop(): File {
        engine.stop()
        val file = currentFile ?: throw IllegalStateException("No recording in progress")
        currentFile = null
        return file
    }
}