package com.argensonix.blurfm.data.repository

import android.util.Log
import com.argensonix.blurfm.data.api.ApiConfig
import com.argensonix.blurfm.data.model.NowPlaying
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * Repository for handling Now Playing data.
 * Coordinates fetching track info from the Now Playing API and artwork from iTunes.
 */
class NowPlayingRepository {
    private val nowPlayingApi = ApiConfig.nowPlayingApiService
    private val iTunesApi = ApiConfig.iTunesApiService

    companion object {
        private const val TAG = "NowPlayingRepository"
        private const val API_TIMEOUT_MS = 5000L // 5 seconds timeout
    }

    /**
     * Fetches the current track information from the Now Playing API.
     *
     * @return NowPlaying object with title and artist, or null if the request fails
     */
    suspend fun fetchNowPlaying(): NowPlaying? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Attempting to fetch now playing info...")
                // Add timeout to prevent hanging
                val result = withTimeout(API_TIMEOUT_MS) {
                    val response = nowPlayingApi.getNowPlaying()
                    val title = response.title ?: "Unknown Track"
                    val artist = response.artist ?: "Unknown Artist"
                    Log.d(TAG, "Fetched now playing: $artist - $title")
                    NowPlaying(
                        title = title,
                        artist = artist,
                        artworkUrl = null
                    )
                }
                result
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                Log.w(TAG, "Timeout fetching now playing - API may not be configured: ${e.message}")
                null
            } catch (e: java.net.UnknownHostException) {
                Log.w(TAG, "Cannot reach now playing API - check URL configuration: ${e.message}")
                null
            } catch (e: java.net.ConnectException) {
                Log.w(TAG, "Connection refused - API may not be running: ${e.message}")
                null
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching now playing: ${e.javaClass.simpleName} - ${e.message}")
                null
            }
        }
    }

    /**
     * Searches for album artwork on iTunes based on artist and title.
     *
     * Implements a small retry/backoff strategy and normalizes the artwork URL to a higher resolution
     * so the UI can display a good-quality image.
     *
     * @param artist The artist name
     * @param title The track title
     * @return URL of the artwork (high resolution) or null if not found
     */
    suspend fun fetchArtwork(artist: String, title: String): String? {
        return withContext(Dispatchers.IO) {
            // Normalize input
            val artistTrim = artist.trim()
            val titleTrim = title.trim()
            // Try several search term variants to improve matching
            val termVariants = listOf(
                "$artistTrim $titleTrim",
                "$titleTrim $artistTrim",
                artistTrim,
                titleTrim
            ).distinct().filter { it.isNotBlank() }

            val maxAttempts = 3
            var attempt = 0
            var found: String? = null

            while (attempt < maxAttempts && found == null) {
                attempt++
                for (term in termVariants) {
                    try {
                        Log.d(TAG, "Searching iTunes for: $term (attempt $attempt)")
                        // Add timeout for iTunes API
                        val candidate = try {
                            withTimeout(API_TIMEOUT_MS) {
                                val response = iTunesApi.searchTrack(term)
                                if (response.resultCount > 0 && response.results.isNotEmpty()) {
                                    val rawArtwork = response.results[0].artworkUrl100
                                    if (!rawArtwork.isNullOrBlank()) {
                                        // Try to return a higher resolution artwork by replacing dimensions
                                        val highRes = rawArtwork.replace("100x100", "600x600")
                                        // Ensure URL uses https
                                        val normalized = when {
                                            highRes.startsWith("http://") -> highRes.replaceFirst("http://", "https://")
                                            highRes.startsWith("https://") -> highRes
                                            else -> "https://$highRes"
                                        }
                                        normalized
                                    } else {
                                        null
                                    }
                                } else {
                                    null
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Exception during iTunes request for term '$term': ${e.javaClass.simpleName} - ${e.message}")
                            null
                        }

                        if (!candidate.isNullOrBlank()) {
                            Log.d(TAG, "Found artwork (normalized) for term '$term': $candidate")
                            found = candidate
                            break
                        } else {
                            Log.d(TAG, "No artwork found for term '$term' (attempt $attempt)")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Search loop error for term '$term' (attempt $attempt): ${e.javaClass.simpleName} - ${e.message}")
                    }
                }

                // Exponential backoff before retrying (only if we will retry)
                if (found == null && attempt < maxAttempts) {
                    val backoff = 200L * (1 shl (attempt - 1)) // 200ms, 400ms, 800ms
                    try {
                        delay(backoff)
                    } catch (_: Exception) {
                        // ignore cancellation during delay
                    }
                }
            }

            if (found == null) {
                Log.d(TAG, "No artwork found after $maxAttempts attempts for variants: ${termVariants.joinToString()}")
            }
            found
        }
    }

    /**
     * Fetches complete Now Playing information including artwork.
     *
     * @return NowPlaying object with title, artist, and artwork URL
     */
    suspend fun fetchCompleteNowPlaying(): NowPlaying? {
        return withContext(Dispatchers.IO) {
            val nowPlaying = fetchNowPlaying()
            if (nowPlaying == null) {
                // English comment: If the configured Now Playing API fails (e.g. not configured),
                // attempt a best-effort artwork lookup on iTunes using a fallback station name so the UI
                // can show a more relevant cover instead of staying with a blank logo.
                Log.w(TAG, "Now Playing API returned null - attempting fallback artwork lookup")
                val fallbackTitle = "Blur FM Radio"
                val fallbackArtist = "Blur FM"
                val artworkUrl = try {
                    fetchArtwork(fallbackArtist, fallbackTitle)
                } catch (e: Exception) {
                    Log.w(TAG, "Fallback artwork lookup failed: ${e.message}")
                    null
                }
                NowPlaying(
                    title = fallbackTitle,
                    artist = fallbackArtist,
                    artworkUrl = artworkUrl
                )
            } else {
                // Fetch artwork from iTunes (don't fail if this doesn't work)
                val artworkUrl = try {
                    fetchArtwork(nowPlaying.artist, nowPlaying.title)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch artwork, continuing without it: ${e.message}")
                    null
                }
                nowPlaying.copy(artworkUrl = artworkUrl)
            }
        }
    }
}
