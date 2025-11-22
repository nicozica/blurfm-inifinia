package com.argensonix.blurfm

/**
 * Configuration object for stream settings.
 * Contains the radio stream URL and other streaming-related constants.
 */
object StreamConfig {
    /**
     * The main streaming URL for Blur FM.
     * Change this URL to point to your radio stream endpoint.
     */
    const val STREAM_URL = "https://live.radiovague.com:8443/blurfm01"

    /**
     * Connection timeout in milliseconds
     */
    const val CONNECTION_TIMEOUT_MS = 10000

    /**
     * Buffer duration for playback
     */
    const val BUFFER_DURATION_MS = 5000
}

