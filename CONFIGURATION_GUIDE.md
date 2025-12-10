# Blur FM - Quick Configuration Guide

## 🚀 Quick Start

### 1. Set Your Now Playing API Endpoint

Open: `app/src/main/java/com/argensonix/blurfm/data/api/ApiConfig.kt`

```kotlin
// Change this line:
private const val NOW_PLAYING_BASE_URL = "https://api.blurfm.com/"

// To your actual API:
private const val NOW_PLAYING_BASE_URL = "https://your-radio-api.com/"
```

### 2. Match Your API Response Format

If your API returns different field names, update:
`app/src/main/java/com/argensonix/blurfm/data/api/NowPlayingModels.kt`

**Example API Response:**
```json
{
  "title": "Un Deux Trois",
  "artist": "MUNYA"
}
```

**Default Model (matches above):**
```kotlin
data class NowPlayingResponse(
    @SerializedName("title")
    val title: String?,
    @SerializedName("artist")
    val artist: String?
)
```

**If your API uses different names:**
```json
{
  "song_name": "Un Deux Trois",
  "artist_name": "MUNYA"
}
```

**Update to:**
```kotlin
data class NowPlayingResponse(
    @SerializedName("song_name")
    val title: String?,
    @SerializedName("artist_name")
    val artist: String?
)
```

### 3. Adjust Update Frequency (Optional)

Open: `app/src/main/java/com/argensonix/blurfm/ui/viewmodel/PlayerViewModel.kt`

```kotlin
// Change this line (line ~47):
private const val NOW_PLAYING_UPDATE_INTERVAL_MS = 20000L // 20 seconds

// Examples:
// 15 seconds: 15000L
// 30 seconds: 30000L
// 1 minute: 60000L
```

### 4. Adjust Fade-In Duration (Optional)

Open: `app/src/main/java/com/argensonix/blurfm/player/AudioPlayerManager.kt`

```kotlin
// Change this line (line ~31):
private const val FADE_IN_DURATION_MS = 800L // 800ms

// Examples:
// Shorter: 500L (half second)
// Longer: 1000L (1 second)
// Very smooth: 1500L (1.5 seconds)
```

## 📝 Testing Without API

If you don't have a Now Playing API yet, the app will still work:
- Title will show: "Blur FM"
- Artist will show: "Loading..."
- No artwork will load (Blur FM logo shows as fallback)
- Audio streaming will work normally

## 🔍 Debugging

### Check Logs for API Calls

Use Android Studio's Logcat with these filters:

**Now Playing Repository:**
```
Tag: NowPlayingRepository
```

**Audio Player:**
```
Tag: AudioPlayerManager
```

**Player ViewModel:**
```
Tag: PlayerViewModel
```

### Common API Response Formats

#### Format 1: Simple (Current Default)
```json
{
  "title": "Track Title",
  "artist": "Artist Name"
}
```

#### Format 2: Nested
```json
{
  "now_playing": {
    "song": "Track Title",
    "artist": "Artist Name"
  }
}
```

For Format 2, update `NowPlayingModels.kt`:
```kotlin
data class NowPlayingWrapper(
    @SerializedName("now_playing")
    val nowPlaying: NowPlayingResponse
)

data class NowPlayingResponse(
    @SerializedName("song")
    val title: String?,
    @SerializedName("artist")
    val artist: String?
)
```

And update `NowPlayingApiService.kt`:
```kotlin
@GET("nowplaying")
suspend fun getNowPlaying(): NowPlayingWrapper
```

And `NowPlayingRepository.kt`:
```kotlin
val response = nowPlayingApi.getNowPlaying().nowPlaying
```

#### Format 3: Array
```json
{
  "tracks": [
    {
      "title": "Track Title",
      "artist": "Artist Name"
    }
  ]
}
```

For Format 3, update accordingly to parse the array and take the first item.

## 🎨 Customization

### Change Fade-In Curve

In `AudioPlayerManager.kt`, you can add an interpolator:

```kotlin
volumeFadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
    duration = FADE_IN_DURATION_MS
    interpolator = AccelerateDecelerateInterpolator() // Smooth curve
    // Or: LinearInterpolator() // Linear
    // Or: DecelerateInterpolator() // Starts fast, ends slow
    addUpdateListener { animation ->
        val volume = animation.animatedValue as Float
        player.volume = volume
    }
    start()
}
```

### Change Update Strategy

Instead of periodic updates, you could:

**Option 1: Update only when playing**
```kotlin
private fun startNowPlayingUpdates() {
    nowPlayingUpdateJob?.cancel()
    nowPlayingUpdateJob = viewModelScope.launch {
        isPlaying.collect { playing ->
            if (playing) {
                while (true) {
                    updateNowPlaying()
                    delay(NOW_PLAYING_UPDATE_INTERVAL_MS)
                }
            }
        }
    }
}
```

**Option 2: Exponential backoff on errors**
```kotlin
private var retryDelayMs = NOW_PLAYING_UPDATE_INTERVAL_MS

private suspend fun updateNowPlaying() {
    try {
        // ... existing code ...
        retryDelayMs = NOW_PLAYING_UPDATE_INTERVAL_MS // Reset on success
    } catch (e: Exception) {
        retryDelayMs = (retryDelayMs * 1.5).toLong() // Increase delay
        delay(retryDelayMs)
    }
}
```

## 🔧 Build & Deploy

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build release APK (for production)
./gradlew assembleRelease
```

## ✅ Pre-Deployment Checklist

- [ ] NOW_PLAYING_BASE_URL is set to production API
- [ ] API response model matches your actual API
- [ ] StreamConfig.STREAM_URL is correct
- [ ] Tested on physical device
- [ ] Tested with poor network connection
- [ ] Verified fade-in effect sounds good
- [ ] Checked now playing updates correctly
- [ ] Verified artwork loads from iTunes
- [ ] Tested error recovery (airplane mode → reconnect)

## 📞 Support

If you encounter issues:

1. Check Logcat for error messages
2. Verify API endpoint is accessible (test with Postman/curl)
3. Ensure INTERNET permission is in AndroidManifest.xml (already added)
4. Test iTunes API separately (should work without configuration)

## 🎵 iTunes API Note

The iTunes artwork search works automatically. No configuration needed. It:
- Searches based on artist + title from your now playing API
- Returns high-res artwork (600x600)
- Falls back to Blur FM logo if not found
- Handles errors gracefully

