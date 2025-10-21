package com.example.audiorecorder

import android.content.Context
import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class RealRecorderEngine(
    private val context: Context
) : RecorderEngine {

    private val mediaRecorder = MediaRecorder(context)

    override fun prepare(output: File) {
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(output.absolutePath)
            try {
                prepare()
            } catch (e: IOException) {
                throw RuntimeException("Failed to prepare MediaRecorder", e)
            }
        }
    }

    override fun start() {
        mediaRecorder.start()
    }

    override fun stop() {
        mediaRecorder.stop()
    }

    override fun release() {
        mediaRecorder.release()
    }
}