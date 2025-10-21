package com.example.audiorecorder

import android.media.MediaMetadataRetriever

/**
 * Real implementation of MediaMetadataRetrieverFactory
 */
class RealMediaMetadataRetrieverFactory : MediaMetadataRetrieverFactory {
    override fun create(): MediaMetadataRetriever {
        return MediaMetadataRetriever()
    }
}