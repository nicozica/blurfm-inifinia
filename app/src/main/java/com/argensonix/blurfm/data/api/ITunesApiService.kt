package com.argensonix.blurfm.data.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for iTunes Search API.
 * Used to fetch album artwork based on track title and artist.
 */
interface ITunesApiService {

    /**
     * Search for tracks in the iTunes catalog.
     *
     * @param term Search term (typically "artist title")
     * @param media Media type (default: "music")
     * @param limit Maximum number of results (default: 1)
     * @return iTunes search response containing track results
     */
    @GET("search")
    suspend fun searchTrack(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("limit") limit: Int = 1
    ): ITunesSearchResponse
}

