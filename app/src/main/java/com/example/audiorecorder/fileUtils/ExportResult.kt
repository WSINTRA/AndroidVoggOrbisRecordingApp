package com.example.audiorecorder.fileUtils

import android.net.Uri

/**
 * Result type for file export operations
 */
sealed class ExportResult {
    data class Success(val uri: Uri) : ExportResult()
    data class Error(val message: String, val cause: Throwable? = null) : ExportResult()
}