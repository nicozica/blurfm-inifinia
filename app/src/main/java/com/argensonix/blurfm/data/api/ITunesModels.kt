package com.argensonix.blurfm.data.api

import com.google.gson.annotations.SerializedName

/**
 * Response from the iTunes Search API.
 *
 * @property resultCount Number of results returned
 * @property results List of track results
 */
data class ITunesSearchResponse(
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: List<ITunesTrack>
)

/**
 * iTunes track data from the search API.
 *
 * @property trackName Name of the track
 * @property artistName Name of the artist
 * @property artworkUrl100 URL for 100x100 artwork (can be scaled up to 600x600)
 */
data class ITunesTrack(
    @SerializedName("trackName")
    val trackName: String?,
    @SerializedName("artistName")
    val artistName: String?,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String?
)

