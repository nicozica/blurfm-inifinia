package com.argensonix.blurfm.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.argensonix.blurfm.data.model.NowPlaying
import com.argensonix.blurfm.data.repository.NowPlayingRepository
import com.argensonix.blurfm.player.AudioPlayerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Player screen.
 * Manages audio playback state and now playing information.
 */
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val audioPlayerManager = AudioPlayerManager(application)
    private val nowPlayingRepository = NowPlayingRepository()

    // Expose player state
    val isPlaying: StateFlow<Boolean> = audioPlayerManager.isPlaying
    val hasError: StateFlow<Boolean> = audioPlayerManager.hasError

    // Now playing information - starts with default content
    private val _nowPlaying = MutableStateFlow(
        NowPlaying(
            title = "Blur FM Radio",
            artist = "Streaming Live",
            artworkUrl = null
        )
    )
    val nowPlaying: StateFlow<NowPlaying> = _nowPlaying.asStateFlow()

    // Loading state for artwork
    private val _isLoadingArtwork = MutableStateFlow(false)
    val isLoadingArtwork: StateFlow<Boolean> = _isLoadingArtwork.asStateFlow()

    private var nowPlayingUpdateJob: Job? = null

    companion object {
        private const val TAG = "PlayerViewModel"
        private const val NOW_PLAYING_UPDATE_INTERVAL_MS = 20000L // 20 seconds
        // Enable periodic Now Playing updates. Set to true to allow fetching dynamic metadata.
        // If your Now Playing API base URL is not configured in ApiConfig, these calls will fail quickly due to short timeouts.
        private const val ENABLE_NOW_PLAYING_UPDATES = true
    }

    init {
        // Start periodic updates only if enabled (requires API configuration)
        if (ENABLE_NOW_PLAYING_UPDATES) {
            startNowPlayingUpdates()
        } else {
            Log.d(TAG, "Now Playing updates disabled - using default content")
        }

        // Perform an initial one-time fetch of now playing on startup. This helps populate artwork/title
        // even if periodic updates are disabled or fail later. Network call uses short timeouts so it won't hang.
        refreshNowPlaying()
    }

    /**
     * Starts periodic updates of the now playing information.
     * Updates every 20 seconds.
     */
    private fun startNowPlayingUpdates() {
        nowPlayingUpdateJob?.cancel()
        nowPlayingUpdateJob = viewModelScope.launch {
            while (true) {
                updateNowPlaying()
                delay(NOW_PLAYING_UPDATE_INTERVAL_MS)
            }
        }
    }

    /**
     * Fetches and updates the current now playing information.
     */
    private suspend fun updateNowPlaying() {
        try {
            Log.d(TAG, "Updating now playing info")
            val newNowPlaying = nowPlayingRepository.fetchCompleteNowPlaying()

            if (newNowPlaying != null) {
                // Only update if the track has actually changed
                val currentNowPlaying = _nowPlaying.value
                if (newNowPlaying.title != currentNowPlaying.title ||
                    newNowPlaying.artist != currentNowPlaying.artist) {

                    Log.d(TAG, "Track changed: ${newNowPlaying.artist} - ${newNowPlaying.title}")
                    _nowPlaying.value = newNowPlaying
                } else if (newNowPlaying.artworkUrl != currentNowPlaying.artworkUrl) {
                    // Update artwork if it changed
                    _nowPlaying.value = newNowPlaying
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating now playing", e)
        }
    }

    /**
     * Manually refreshes the now playing information.
     */
    fun refreshNowPlaying() {
        viewModelScope.launch {
            updateNowPlaying()
        }
    }

    /**
     * Toggles between play and pause states.
     */
    fun togglePlayPause() {
        audioPlayerManager.togglePlayPause()
    }

    /**
     * Starts audio playback.
     */
    fun play() {
        audioPlayerManager.play()
    }

    /**
     * Pauses/stops audio playback.
     */
    fun pause() {
        audioPlayerManager.pause()
    }

    /**
     * Retries connection after an error.
     */
    fun retry() {
        audioPlayerManager.retry()
    }

    /**
     * Cleanup when ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        nowPlayingUpdateJob?.cancel()
        audioPlayerManager.releasePlayer()
        Log.d(TAG, "ViewModel cleared, player released")
    }
}
