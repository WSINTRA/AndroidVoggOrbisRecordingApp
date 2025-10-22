package com.example.audiorecorder.recorderUtils

import java.io.File

interface RecorderEngine {
    fun prepare(output: File)
    fun start()
    fun stop()
    fun release()
}