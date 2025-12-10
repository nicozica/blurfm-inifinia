package com.argensonix.blurfm.data.api
import retrofit2.http.GET
/**
 * Retrofit API service for fetching Now Playing information from Blur FM.
 * 
 * TODO: Replace the base URL in ApiConfig with your actual Now Playing endpoint.
 */
interface NowPlayingApiService {
    /**
     * Fetch the currently playing track information.
     *
     * @return Now playing response with title and artist
     */
    @GET("nowplaying")
    suspend fun getNowPlaying(): NowPlayingResponse
}
