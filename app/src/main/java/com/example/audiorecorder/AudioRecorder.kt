package com.example.audiorecorder

import java.io.File

class AudioRecorder (
    private val engine: RecorderEngine,
    private val outputFactory: OutputFileFactory
) {
    fun start(): File {
        val file = outputFactory.nextFile()
        engine.prepare(file)
        engine.start()
        return file
    }
    fun stop(): File {
        engine.stop()
        return outputFactory.nextFile()
    }
}