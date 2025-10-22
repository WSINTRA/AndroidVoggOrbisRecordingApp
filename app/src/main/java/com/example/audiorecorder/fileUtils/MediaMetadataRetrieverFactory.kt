package com.example.audiorecorder.fileUtils

import android.media.MediaMetadataRetriever

/**
 * Factory for creating MediaMetadataRetriever instances
 * This abstraction allows for easier testing
 */
interface MediaMetadataRetrieverFactory {
    fun create(): MediaMetadataRetriever
}