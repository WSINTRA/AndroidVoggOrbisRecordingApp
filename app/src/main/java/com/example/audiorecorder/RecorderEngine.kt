package com.example.audiorecorder

import java.io.File

interface RecorderEngine {
    fun prepare(output: File)
    fun start()
    fun stop()
    fun release()
}