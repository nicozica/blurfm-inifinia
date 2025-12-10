package com.argensonix.blurfm.data.model

/**
 * Data model representing the current playing track information.
 *
 * @property title The title of the currently playing track
 * @property artist The artist name of the currently playing track
 * @property artworkUrl The URL of the album artwork (can be null if not found)
 */
data class NowPlaying(
    val title: String = "Blur FM",
    val artist: String = "Loading...",
    val artworkUrl: String? = null
)

