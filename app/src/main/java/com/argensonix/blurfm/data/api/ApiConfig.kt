package com.argensonix.blurfm.data.api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
/**
 * Configuration object for API services.
 * Provides singleton instances of Retrofit API services.
 */
object ApiConfig {
    /**
     * Base URL for the iTunes Search API.
     */
    private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
    /**
     * Base URL for the Now Playing API.
     * TODO: Replace this with your actual Now Playing API endpoint.
     * Example: "https://api.blurfm.com/" or "https://yourserver.com/api/"
     */
    private const val NOW_PLAYING_BASE_URL = "https://api.blurfm.com/"
    /**
     * Creates an OkHttpClient with logging interceptor for debugging.
     * Uses short timeouts to prevent hanging if API is not configured.
     */
    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC // Less verbose
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)  // Short timeout
            .readTimeout(5, TimeUnit.SECONDS)     // Short timeout
            .build()
    }
    /**
     * Provides the iTunes API service instance.
     */
    val iTunesApiService: ITunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }
    /**
     * Provides the Now Playing API service instance.
     */
    val nowPlayingApiService: NowPlayingApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NOW_PLAYING_BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NowPlayingApiService::class.java)
    }
}
