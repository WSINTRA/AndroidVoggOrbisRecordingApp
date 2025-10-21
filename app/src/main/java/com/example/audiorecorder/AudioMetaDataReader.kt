package com.example.audiorecorder

import java.io.File

/**
 * Reads metadata from audio files
 */
interface AudioMetadataReader {
    /**
     * Gets the duration of an audio file in milliseconds
     */
    fun getDuration(file: File): Long
}