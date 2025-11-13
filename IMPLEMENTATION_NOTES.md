# Blur FM Android App - Implementation Notes

## Overview
This implementation provides the complete UI structure for the Blur FM radio app using Kotlin, Jetpack Compose, and Material 3.

## What Was Created

### 1. MainActivity.kt (241 lines)
**Location:** `app/src/main/java/com/argensonix/blurfm/MainActivity.kt`

Complete implementation including:

#### `MainActivity` class
- Single activity setup with `enableEdgeToEdge()`
- Calls `setContent { BlurFmApp() }` as requested

#### `BlurFmApp()` composable
- Wraps content in `BlurFMTheme` (Material 3)
- Uses Jetpack Navigation with `NavHost`
- Implements fade transitions between screens
- Routes: "splash" → "player"

#### `SplashScreen()` composable
Features:
- Full-screen background image (`R.drawable.bg_splash`)
- Dark blue overlay (`Color(0xCC001F3F)`) for contrast
- Blur FM logo centered near bottom (200dp size)
- Auto-navigation after 3 seconds using `LaunchedEffect`

#### `PlayerScreen()` composable
Features:
- Full-screen blurred grass background (`R.drawable.bg_player`)
- Semi-transparent dark overlay (`Color(0x88000000)`) for text contrast
- **Top section:** Blur FM logo (120dp size)
- **Middle section:**
  - Square album cover card (280dp) with elevation
  - Song title: "Un Deux Trois" (32sp, bold, white)
  - Artist name: "MUNYA" (18sp, uppercase, 2sp letter spacing)
- **Bottom section:** Circular play/pause button (80dp)
  - Uses local `isPlaying` state with `remember { mutableStateOf(false) }`
  - Toggles between `R.drawable.ic_play` and `R.drawable.ic_pause`
  - Pink/magenta color from Material theme

### 2. Drawable Resources
**Location:** `app/src/main/res/drawable/`

Created placeholder vector drawables:

- **`bg_splash.xml`** - Gradient background (dark blue theme)
- **`bg_player.xml`** - Green gradient simulating blurred grass
- **`logo_blurfm.xml`** - Circular magenta logo with "BF" text
- **`cover_placeholder.xml`** - Purple album cover with music note icon
- **`ic_play.xml`** - Standard play triangle icon
- **`ic_pause.xml`** - Standard pause bars icon

### 3. Dependencies Added
**Location:** `gradle/libs.versions.toml` and `app/build.gradle.kts`

- Added `androidx-navigation-compose` version 2.7.7
- Already included: Material 3, Compose UI, Activity Compose

## Code Characteristics

✅ **All comments in English** as requested
✅ **Modern Compose structures:** Box, Column, modifiers, remember, LaunchedEffect
✅ **Material 3 theming** applied throughout
✅ **Single-activity architecture** with setContent
✅ **No streaming logic** - only local UI state management
✅ **Edge-to-edge display** support enabled
✅ **Type-safe navigation** with Jetpack Compose Navigation

## Design Specifications Implemented

### Splash Screen
- ✅ Full-screen photo background
- ✅ Dark blue overlay
- ✅ Logo centered near bottom
- ✅ Auto-transition to player

### Player Screen
- ✅ Blurred grass background with dark overlay
- ✅ Logo at top center
- ✅ Square album artwork card
- ✅ Song title in large bold typography
- ✅ Artist name in smaller uppercase text
- ✅ Circular play/pause button at bottom
- ✅ Proper spacing and alignment

## Usage

### To Run:
```bash
./gradlew installDebug
```

### To Replace Placeholder Assets:
Simply replace the drawable XML files with your actual image assets:
- `res/drawable/bg_splash` - Your splash screen photo
- `res/drawable/bg_player` - Your blurred grass photo
- `res/drawable/logo_blurfm` - Your actual logo
- `res/drawable/cover_placeholder` - Default album artwork
- `res/drawable/ic_play` - Your play icon SVG
- `res/drawable/ic_pause` - Your pause icon SVG

### To Update Track Info:
Edit the `PlayerScreen()` composable in `MainActivity.kt`:
```kotlin
Text(text = "Un Deux Trois", ...) // Change song title
Text(text = "MUNYA", ...)          // Change artist name
```

## Next Steps for Full Implementation

1. **Add actual images:** Replace placeholder drawables with real assets
2. **Implement streaming:** Add media player logic to handle radio stream
3. **Add metadata updates:** Connect to radio API for live track info
4. **Add error handling:** Handle network errors gracefully
5. **Add loading states:** Show loading indicator while connecting
6. **Add notifications:** Media notification with play/pause controls
7. **Add background playback:** Service for playing when app is in background

## Architecture Notes

- **MVVM ready:** Easy to add ViewModels for state management
- **Composable structure:** Can easily extract components to separate files
- **Navigation ready:** Can add more screens easily
- **Theme ready:** Colors and typography defined in `ui/theme/` package

## Testing

Due to build environment limitations, the code hasn't been compiled yet, but:
- ✅ Syntax follows Kotlin and Compose best practices
- ✅ All resource references are defined
- ✅ Navigation structure is standard
- ✅ State management follows Compose patterns

**Recommendation:** Test in your local Android Studio environment where Gradle can properly resolve dependencies.
