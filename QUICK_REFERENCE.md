# Blur FM - Quick Reference

## 📂 File Structure

```
app/src/main/java/com/argensonix/blurfm/
├── MainActivity.kt         ← Main activity with player implementation
├── StreamConfig.kt         ← Stream URL configuration
└── ui/
    └── theme/
        └── BlurFMTheme     ← Material 3 theme

app/src/main/res/
└── drawable/
    ├── bg_player.webp           ← Player background
    ├── bg_splash.webp           ← Splash background
    ├── cover_placeholder.webp   ← Album cover
    ├── logo_blurfm.webp         ← App logo
    ├── ic_play.webp             ← Play icon
    └── ic_pause.webp            ← Pause icon
```

## 🔧 Configuration

### Change Stream URL
**File**: `app/src/main/java/com/argensonix/blurfm/StreamConfig.kt`
```kotlin
const val STREAM_URL = "https://live.radiovague.com:8443/blurfm01"
```

### Dependencies
**File**: `gradle/libs.versions.toml`
```toml
[versions]
exoplayer = "2.19.1"

[libraries]
exoplayer-core = { group = "com.google.android.exoplayer", name = "exoplayer-core", version.ref = "exoplayer" }
```

**File**: `app/build.gradle.kts`
```kotlin
implementation(libs.exoplayer.core)
```

## 🎮 Key Components

### PlayerScreen Composable
- **Location**: `MainActivity.kt` (line ~160)
- **Purpose**: Main player UI with ExoPlayer integration
- **Features**:
  - Audio streaming
  - Play/Pause control
  - Error handling
  - Automatic resource cleanup

### ExoPlayer Initialization
```kotlin
val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
        val mediaItem = MediaItem.fromUri(StreamConfig.STREAM_URL)
        setMediaItem(mediaItem)
        prepare()
        addListener(/* Player.Listener */)
    }
}
```

### Resource Cleanup
```kotlin
DisposableEffect(exoPlayer) {
    onDispose {
        exoPlayer.release()
    }
}
```

## 🎨 UI Specifications

### Splash Screen
- **Duration**: 3 seconds
- **Background**: `bg_splash.webp`
- **Logo Size**: 200dp
- **Logo Position**: Bottom center, 120dp from bottom

### Player Screen
- **Background**: `bg_player.webp` with 0x88000000 overlay
- **Logo Size**: 120dp (top)
- **Album Cover**: 280dp card with 8dp elevation
- **Play Button**: 72dp circle with 32dp icon
- **Colors**: White text on dark background

## 🔊 Player States

| State | Description | Icon |
|-------|-------------|------|
| **Stopped** | Initial state, ready to play | `ic_play` |
| **Playing** | Actively streaming audio | `ic_pause` |
| **Paused** | Playback paused | `ic_play` |
| **Error** | Stream failed to load | `ic_play` + Snackbar |

## 🐛 Common Issues & Fixes

### 1. Unresolved Reference Error
```bash
# In Android Studio
File → Invalidate Caches / Restart
```

### 2. Build Failure (Java Version)
```bash
# Check Java version
java -version

# Should be Java 11 or 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### 3. Stream Not Playing
- ✅ Check internet permission in manifest
- ✅ Verify stream URL is accessible
- ✅ Check device internet connection
- ✅ Look for error in Snackbar message

### 4. IDE Not Recognizing ExoPlayer
```bash
./gradlew --stop
./gradlew clean build --refresh-dependencies
```

## 📱 Testing Checklist

- [ ] App launches with splash screen
- [ ] Navigates to player after 3 seconds
- [ ] Play button starts stream
- [ ] Icon changes to pause when playing
- [ ] Pause button stops stream
- [ ] Error shows Snackbar if stream fails
- [ ] Resources released when app closes
- [ ] No memory leaks

## 🚀 Build Commands

```bash
# Clean
./gradlew clean

# Build Debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test

# Full check
./gradlew check
```

## 📝 Code Comments Language

✅ **All comments are in English**

Examples:
```kotlin
// Initialize ExoPlayer with proper lifecycle management
// Listen to player state changes to update UI
// Release player resources when composable leaves composition
// Toggle playback state
// Show error message to user
```

## 🔒 Permissions

### Already Declared
- ✅ `android.permission.INTERNET`

### Not Needed (Yet)
- ❌ `WAKE_LOCK` (for keeping device awake)
- ❌ `FOREGROUND_SERVICE` (for background playback)

## 🎯 Next Features (Optional)

1. **Metadata Display**: Show current track from stream metadata
2. **Notification**: Media controls in notification shade
3. **Background Playback**: Continue playing when app is minimized
4. **Loading State**: Show buffering indicator
5. **Volume Control**: Add volume slider
6. **Sleep Timer**: Auto-stop after X minutes
7. **Audio Focus**: Pause on phone calls

## 📚 Documentation Files

- `IMPLEMENTATION_SUMMARY.md` - Complete implementation details
- `TROUBLESHOOTING.md` - Detailed troubleshooting guide
- `README.md` - Project overview
- `QUICK_START.md` - Getting started guide

## 🎉 Status

**✅ Implementation Complete**
- All requirements met
- Code clean and documented
- Ready for testing and deployment

