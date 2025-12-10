package com.argensonix.blurfm.data.api
import com.google.gson.annotations.SerializedName
/**
 * Response model for the Now Playing API.
 * This is a placeholder structure - adjust fields to match your actual API response.
 *
 * @property title Current track title
 * @property artist Current track artist
 */
data class NowPlayingResponse(
    @SerializedName("title")
    val title: String?,
    @SerializedName("artist")
    val artist: String?
)
