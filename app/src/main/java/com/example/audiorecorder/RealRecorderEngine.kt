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
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(48000) // Professional standard
            setAudioEncodingBitRate(320000) // Maximum quality
            setAudioChannels(2) // Stereo if supported
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