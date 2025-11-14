package com.example.audiorecorder.fileUtils

import java.io.File

/**
 * Interface for exporting audio files to shared storage
 */
interface FileExporter {
    /**
     * Exports an audio file to the device's Music folder
     *
     * @param sourceFile The file to export
     * @param displayName The desired name for the exported file (without extension)
     * @return ExportResult.Success with URI if successful, ExportResult.Error otherwise
     */
    fun exportToMusic(sourceFile: File, displayName: String): ExportResult
}

