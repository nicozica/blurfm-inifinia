# Blur FM - Troubleshooting & Build Instructions

## IDE Sync Issues

If you see "Unresolved reference 'exoplayer'" error in the IDE:

### Solution 1: Invalidate Caches
1. In Android Studio/IntelliJ IDEA: **File → Invalidate Caches / Restart**
2. Select "Invalidate and Restart"
3. Wait for the IDE to restart and sync

### Solution 2: Manual Gradle Sync
```bash
cd /home/nico/htdocs/argensonix/blurfm/blurfm-infinia
./gradlew --stop
./gradlew clean build --refresh-dependencies
```

### Solution 3: Check Java Version
The build error "25.0.1" suggests a Java version parsing issue. Ensure you're using Java 11 or 17:

```bash
# Check Java version
java -version

# If needed, set Java 17 (example for Linux)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

### Solution 4: Alternative Dependency Declaration
If the version catalog isn't working, you can temporarily add ExoPlayer directly in `app/build.gradle.kts`:

```kotlin
dependencies {
    // ...existing dependencies...
    
    // Add this instead of libs.exoplayer.core
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
}
```

## Build Commands

### Clean Build
```bash
./gradlew clean
```

### Debug APK
```bash
./gradlew assembleDebug
```

### Install on Device
```bash
./gradlew installDebug
```

### Run All Checks
```bash
./gradlew check
```

## Common Issues

### 1. Internet Permission
✅ Already configured in `AndroidManifest.xml`

### 2. Stream URL Not Working
- Open `StreamConfig.kt`
- Verify the URL is correct: `https://live.radiovague.com:8443/blurfm01`
- Test the URL in a browser or with curl:
```bash
curl -I https://live.radiovague.com:8443/blurfm01
```

### 3. HTTPS/SSL Issues
If the stream uses HTTPS with custom certificates, you may need to add network security config.

### 4. Audio Focus
The current implementation doesn't handle audio focus. For production, consider:
- Handling phone calls (pause when call starts)
- Handling other audio apps
- Adding audio focus request

## Testing the App

### In Android Studio
1. File → Sync Project with Gradle Files
2. Build → Make Project
3. Run → Run 'app'

### Expected Behavior
1. **Splash Screen**: Shows Blur FM logo for 3 seconds
2. **Player Screen**: 
   - Large album cover placeholder
   - Track info ("Un Deux Trois" by "MUNYA")
   - Circular play button at bottom
3. **Play Button**: 
   - Tap to start streaming
   - Icon changes to pause
   - Tap again to pause
4. **Error Handling**:
   - If stream fails, Snackbar appears with error
   - "Retry" button to attempt reconnection

## Production Checklist

Before releasing:
- [ ] Test with actual stream URL
- [ ] Test on various Android versions (API 24+)
- [ ] Test with slow/unstable network
- [ ] Test with airplane mode toggle
- [ ] Verify battery usage is acceptable
- [ ] Add app icon and splash screen
- [ ] Configure ProGuard rules for ExoPlayer
- [ ] Add analytics (optional)
- [ ] Test memory leaks (use LeakCanary)
- [ ] Add crash reporting (Firebase Crashlytics)

## ExoPlayer ProGuard Rules

If using ProGuard/R8 for release builds, add to `proguard-rules.pro`:

```proguard
# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**
```

## Current Configuration

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 14)
- **Compile SDK**: 35
- **ExoPlayer**: 2.19.1
- **Kotlin**: 2.0.21
- **Compose BOM**: 2024.09.00

