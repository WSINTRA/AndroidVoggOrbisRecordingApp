package com.example.audiorecorder.playerUtils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.util.concurrent.atomic.AtomicBoolean

/**
 * PlayerEngine implementation using AudioTrack and MediaCodec
 * for precise buffer control during real-time audio processing.
 *
 * @param context Android context for accessing resources
 * @param bufferSizeInFrames Buffer size in frames (samples per channel).
 *                           At 48kHz: 4096 frames = ~85ms, 2048 frames = ~42ms
 */
class RealAudioTrackPlayerEngine(
    private val context: Context,
    private val bufferSizeInFrames: Int
) : PlayerEngine {

    private var dataSourcePath: String? = null
    private var mediaExtractor: MediaExtractor? = null
    private var mediaCodec: MediaCodec? = null
    private var audioFormat: MediaFormat? = null
    private var audioTrack: AudioTrack? = null
    private var playbackThread: Thread? = null

    private val isPlayingState = AtomicBoolean(false)

    private val shouldStopPlayback = AtomicBoolean(false)
    private var currentPositionMs = 0
    private var completionListener: (() -> Unit)? = null
    init {
        // Initialize AudioTrack early to get a valid audio session ID
        // We'll reconfigure it properly during prepare() based on the audio format
        initializeAudioTrack(
            sampleRate = 48000, // Default, will be updated in prepare()
            channelConfig = AudioFormat.CHANNEL_OUT_STEREO
        )
    }
    /**
     * Calculates buffer size in bytes from frames
     *
     * @param frames Number of frames (samples per channel)
     * @param channelCount Number of audio channels (1=mono, 2=stereo)
     * @return Buffer size in bytes for 16-bit PCM audio
     */
    private fun calculateBufferSizeInBytes(frames: Int, channelCount: Int): Int {
        // 16-bit PCM = 2 bytes per sample
        return frames * channelCount * 2
    }

    private fun initializeAudioTrack(sampleRate: Int, channelConfig: Int) {
        // Calculate buffer size in bytes
        // bufferSizeInFrames * bytesPerFrame
        // bytesPerFrame = channels * bytesPerSample (2 bytes for 16-bit PCM)
        val channelCount = if (channelConfig == AudioFormat.CHANNEL_OUT_STEREO) 2 else 1
        val bufferSizeInBytes = calculateBufferSizeInBytes(bufferSizeInFrames, channelCount)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(channelConfig)
                    .build()
            )
            .setBufferSizeInBytes(bufferSizeInBytes)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    override fun getAudioId(): Int {
        return audioTrack?.audioSessionId ?: 0
    }

    override fun setDataSource(path: String) {
        dataSourcePath = path
    }

    override fun prepare() {
        val path = dataSourcePath
            ?: throw IllegalStateException("Data source not set")

        // Initialize MediaExtractor to read the audio file
        mediaExtractor = MediaExtractor().apply {
            setDataSource(path)
        }

        // Find the audio track
        val extractor = mediaExtractor!!
        var trackIndex = -1
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith("audio/")) {
                trackIndex = i
                audioFormat = format
                break
            }
        }

        if (trackIndex < 0) {
            throw IllegalArgumentException("No audio track found in file")
        }

        extractor.selectTrack(trackIndex)

        // Create MediaCodec decoder
        val mime = audioFormat!!.getString(MediaFormat.KEY_MIME)!!
        mediaCodec = MediaCodec.createDecoderByType(mime).apply {
            configure(audioFormat, null, null, 0)
        }
    }

    override fun start() {
        if (isPlayingState.get()) {
            return // Already playing
        }

        val codec = mediaCodec ?: throw IllegalStateException("Not prepared")
        val track = audioTrack ?: throw IllegalStateException("AudioTrack not initialized")

        // Start the codec
        codec.start()

        // Start AudioTrack playback
        track.play()

        isPlayingState.set(true)
        shouldStopPlayback.set(false)

        // Start playback thread
        playbackThread = Thread {
            decodeAndPlay()
        }.apply {
            name = "AudioPlaybackThread"
            start()
        }
    }

    override fun isPlaying(): Boolean {
        return isPlayingState.get()
    }

    /**
     * Main decode and playback loop
     * Runs on background thread
     */
    private fun decodeAndPlay() {
        val codec = mediaCodec ?: return
        val track = audioTrack ?: return
        val extractor = mediaExtractor ?: return

        val bufferInfo = MediaCodec.BufferInfo()
        var isInputDone = false
        var isOutputDone = false

        try {
            while (!shouldStopPlayback.get() && !isOutputDone) {
                // Feed input to decoder
                if (!isInputDone) {
                    val inputBufferIndex = codec.dequeueInputBuffer(10000)
                    if (inputBufferIndex >= 0) {
                        val inputBuffer = codec.getInputBuffer(inputBufferIndex)
                        if (inputBuffer != null) {
                            val sampleSize = extractor.readSampleData(inputBuffer, 0)
                            if (sampleSize < 0) {
                                // End of stream
                                codec.queueInputBuffer(
                                    inputBufferIndex, 0, 0, 0,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                )
                                isInputDone = true
                            } else {
                                val presentationTimeUs = extractor.sampleTime
                                codec.queueInputBuffer(
                                    inputBufferIndex, 0, sampleSize,
                                    presentationTimeUs, 0
                                )
                                extractor.advance()
                            }
                        }
                    }
                }

                // Get decoded output
                val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 10000)
                if (outputBufferIndex >= 0) {
                    val outputBuffer = codec.getOutputBuffer(outputBufferIndex)
                    if (outputBuffer != null && bufferInfo.size > 0) {
                        // Write PCM data to AudioTrack
                        val pcmData = ByteArray(bufferInfo.size)
                        outputBuffer.get(pcmData)
                        outputBuffer.clear()

                        track.write(pcmData, 0, pcmData.size)
                    }

                    codec.releaseOutputBuffer(outputBufferIndex, false)

                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        isOutputDone = true
                    }
                }
            }

            // Wait for AudioTrack to finish playing buffered audio
            if (isOutputDone && !shouldStopPlayback.get()) {
                // Playback completed naturally (not stopped by user)
                waitForAudioTrackToFinish(track)

                // Update state
                isPlayingState.set(false)

                // Call completion listener on main thread
                completionListener?.let { listener ->
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        listener()
                    }
                }
            } else {
                // Playback was stopped by user
                isPlayingState.set(false)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            isPlayingState.set(false)
        }
    }
    /**
     * Waits for AudioTrack to finish playing all buffered audio
     *
     * Calculates expected drain time based on buffer size and sample rate
     */
    private fun waitForAudioTrackToFinish(track: AudioTrack) {
        try {
            // Calculate approximate time to drain the buffer
            // bufferSizeInFrames / sampleRate = seconds to drain
            val format = audioFormat
            if (format != null) {
                val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                val drainTimeMs = (bufferSizeInFrames * 1000L) / sampleRate

                // Wait for buffer to drain, plus a small margin
                Thread.sleep(drainTimeMs + 50)
            } else {
                // Fallback: wait a fixed time
                Thread.sleep(200)
            }

        } catch (e: Exception) {
            // Ignore errors during wait
        }
    }
    override fun release() {
        // Stop everything
        shouldStopPlayback.set(true)
        isPlayingState.set(false)

        playbackThread?.join(1000)
        playbackThread = null

        // Release all resources
        audioTrack?.release()
        audioTrack = null

        mediaCodec?.release()
        mediaCodec = null

        mediaExtractor?.release()
        mediaExtractor = null

        audioFormat = null
        dataSourcePath = null
    }

    override fun pause() {
        if (!isPlayingState.get()) {
            return // Not playing
        }

        audioTrack?.pause()
        isPlayingState.set(false)

        // Note: We don't stop the decode thread or codec here
        // This allows resuming from the same position
    }

    override fun stop() {
        // Stop playback thread
        shouldStopPlayback.set(true)
        isPlayingState.set(false)

        // Wait for playback thread to finish
        playbackThread?.join(1000)
        playbackThread = null

        // Stop and flush AudioTrack
        audioTrack?.apply {
            pause()
            flush()
            stop()
        }

        // Stop and flush MediaCodec
        mediaCodec?.apply {
            try {
                stop()
                flush()
            } catch (e: IllegalStateException) {
                // Codec might not be started, ignore
            }
        }

        // Reset extractor to beginning
        mediaExtractor?.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
    }



    override fun reset() {
        // Stop playback if running
        stop()

        // Release codec and extractor
        mediaCodec?.release()
        mediaCodec = null

        mediaExtractor?.release()
        mediaExtractor = null

        audioFormat = null
        dataSourcePath = null
        completionListener = null  // ADD THIS - clear listener on reset

        // Reset AudioTrack to initial state (48kHz stereo default)
        audioTrack?.release()
        initializeAudioTrack(
            sampleRate = 48000,
            channelConfig = AudioFormat.CHANNEL_OUT_STEREO
        )

        shouldStopPlayback.set(false)
    }

    override fun seekTo(position: Int) {
        TODO("Not yet implemented")
    }

    override fun getCurrentPosition(): Int {
        TODO("Not yet implemented")
    }

    override fun getDuration(): Int {
        TODO("Not yet implemented")
    }


    override fun setOnCompletionListener(listener: () -> Unit) {
        completionListener = listener
    }
}