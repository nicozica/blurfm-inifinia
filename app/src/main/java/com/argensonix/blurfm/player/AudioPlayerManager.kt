package com.argensonix.blurfm.player
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import com.argensonix.blurfm.StreamConfig
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * Manager class for handling audio streaming with ExoPlayer.
 * Handles player lifecycle, fade-in effects, and stream reconnection.
 */
class AudioPlayerManager(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private var volumeFadeAnimator: ValueAnimator? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError.asStateFlow()
    companion object {
        private const val TAG = "AudioPlayerManager"
        private const val FADE_IN_DURATION_MS = 800L
    }
    /**
     * Initializes the ExoPlayer instance with listeners.
     */
    private fun initializePlayer() {
        if (exoPlayer != null) return
        Log.d(TAG, "Initializing player")
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            // Prepare the media source with the stream URL
            val mediaItem = MediaItem.fromUri(StreamConfig.STREAM_URL)
            setMediaItem(mediaItem)
            // Add listener for player state changes
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            Log.d(TAG, "Player ready")
                            _hasError.value = false
                        }
                        Player.STATE_ENDED -> {
                            Log.d(TAG, "Playback ended")
                            _isPlaying.value = false
                        }
                        Player.STATE_BUFFERING -> {
                            Log.d(TAG, "Buffering...")
                        }
                    }
                }
                override fun onIsPlayingChanged(playing: Boolean) {
                    _isPlaying.value = playing
                    Log.d(TAG, "Is playing: $playing")
                }
                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "Player error: ${error.message}", error)
                    _hasError.value = true
                    _isPlaying.value = false
                }
            })
            prepare()
        }
    }
    /**
     * Starts playback with a smooth fade-in effect.
     * Always connects to the live stream (no buffered content).
     */
    fun play() {
        Log.d(TAG, "Play requested")
        // Initialize or recreate player to ensure we connect to live stream
        if (exoPlayer == null || _hasError.value) {
            releasePlayer()
            initializePlayer()
        }
        exoPlayer?.let { player ->
            // Cancel any ongoing fade animation
            volumeFadeAnimator?.cancel()
            // Start with volume at 0
            player.volume = 0f
            player.playWhenReady = true
            // Fade in the volume smoothly
            volumeFadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = FADE_IN_DURATION_MS
                addUpdateListener { animation ->
                    val volume = animation.animatedValue as Float
                    player.volume = volume
                }
                start()
            }
            Log.d(TAG, "Started playback with fade-in")
        }
    }
    /**
     * Stops playback and releases the buffer.
     * Next play() will reconnect to the live stream.
     */
    fun stop() {
        Log.d(TAG, "Stop requested")
        // Cancel fade animation
        volumeFadeAnimator?.cancel()
        exoPlayer?.let { player ->
            player.playWhenReady = false
            player.stop()
            // Don't release the player here to avoid recreating it constantly
            // Just stop playback and clear the buffer
        }
    }
    /**
     * Pauses playback (stops and clears buffer for live streaming).
     * For live streams, pause behaves like stop.
     */
    fun pause() {
        stop() // For live streaming, pause = stop
    }
    /**
     * Releases the ExoPlayer instance and clears the buffer.
     * Should be called when the player is no longer needed.
     */
    fun releasePlayer() {
        Log.d(TAG, "Releasing player")
        volumeFadeAnimator?.cancel()
        volumeFadeAnimator = null
        exoPlayer?.let { player ->
            player.stop()
            player.release()
        }
        exoPlayer = null
        _isPlaying.value = false
    }
    /**
     * Toggles between play and pause states.
     */
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }
    /**
     * Prepares the player for a fresh connection.
     * Used when retrying after an error.
     */
    fun retry() {
        Log.d(TAG, "Retry requested")
        _hasError.value = false
        releasePlayer()
        play()
    }
}
