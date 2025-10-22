package com.example.audiorecorder.fileUtils

import java.io.File

/**
 * Provides access to file system directories
 */
interface FileProvider {
    /**
     * Returns the directory where recordings are stored
     */
    fun getRecordingsDirectory(): File
}