# Blur FM UI Layout Guide

## Visual Structure

### Splash Screen (3 seconds)

```
┌─────────────────────────────────────┐
│                                     │
│     [Dark Blue Photo Background]    │
│                                     │
│                                     │
│                                     │
│                                     │
│                                     │
│                                     │
│                                     │
│          ┌─────────────┐           │
│          │             │           │
│          │  Blur FM    │           │
│          │   Logo      │           │
│          │             │           │
│          └─────────────┘           │
│                                     │
│                                     │
│                                     │
└─────────────────────────────────────┘
```

**Components:**
- Full-screen background image with dark blue overlay (#001F3F with 80% opacity)
- Logo: 200dp circular image centered near bottom (120dp from bottom)
- Auto-navigates to Player after 3 seconds

---

### Player Screen

```
┌─────────────────────────────────────┐
│   [Blurred Grass Background]        │
│                                     │
│          ┌─────────────┐           │ Top Section
│          │  Blur FM    │           │
│          │   Logo      │           │
│          └─────────────┘           │
│            (120dp)                  │
│                                     │
│                                     │
│       ┌─────────────────┐          │ Middle Section
│       │                 │          │
│       │  Album Cover    │          │
│       │   (280x280dp)   │          │
│       │                 │          │
│       └─────────────────┘          │
│                                     │
│       "Un Deux Trois"               │ Song Title
│       (32sp, bold, white)           │
│                                     │
│          "MUNYA"                    │ Artist Name
│       (18sp, uppercase)             │
│                                     │
│                                     │
│             ┌───┐                   │ Bottom Section
│             │ ▶ │                   │
│             └───┘                   │ Play/Pause Button
│            (80dp)                   │
│                                     │
└─────────────────────────────────────┘
```

**Components:**
- Background: Blurred grass image with dark overlay (50% opacity black)
- Logo: 120dp at top center, 48dp padding from top
- Album Cover: 280dp square card with 8dp elevation
- Song Title: 32sp bold, white color
- Artist Name: 18sp, uppercase, 80% white opacity, 2sp letter spacing
- Play Button: 80dp circular button with pink/magenta background
- Button Icon: 40dp play/pause icon in white

---

## Color Scheme

### Splash Screen
- **Background Overlay:** `Color(0xCC001F3F)` - Dark blue with 80% opacity
- **Logo:** Magenta/Pink circular background with white text

### Player Screen
- **Background Overlay:** `Color(0x88000000)` - Black with 53% opacity
- **Logo:** Same as splash
- **Album Cover:** Purple gradient with white music note
- **Text:** White and semi-transparent white
- **Button:** Material theme primary color (magenta/pink)
- **Icons:** White

---

## Spacing & Sizing

### Splash Screen
- Logo size: 200dp
- Bottom padding: 120dp

### Player Screen
- Top section padding: 48dp from top
- Logo size: 120dp
- Album cover: 280dp × 280dp
- Spacer after cover: 32dp
- Spacer between title and artist: 8dp
- Button size: 80dp diameter
- Button icon: 40dp
- Bottom padding: 48dp
- Side padding: 32dp throughout

---

## Typography

### Song Title
```kotlin
style = MaterialTheme.typography.headlineLarge.copy(
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp
)
```

### Artist Name
```kotlin
style = MaterialTheme.typography.titleMedium.copy(
    fontSize = 18.sp,
    letterSpacing = 2.sp
)
```

---

## Navigation Flow

```
[Splash Screen]
      │
      │ (3 seconds auto)
      │
      ▼
[Player Screen]
      │
      │ (user interaction)
      ▼
   [Toggle Play/Pause]
```

---

## State Management

### Global State
- Navigation state managed by `NavController`

### Local State (Player Screen)
```kotlin
var isPlaying by remember { mutableStateOf(false) }
```
- `false` = Shows Play icon
- `true` = Shows Pause icon
- Toggles on button click

---

## Animations

### Screen Transitions
- **Type:** Fade in/out
- **Duration:** 300ms
- **Easing:** Default tween

### Splash Screen
- **Auto-navigation:** Delayed by 3000ms using `LaunchedEffect`

---

## Responsive Design

All layouts use:
- `fillMaxSize()` for full-screen coverage
- `padding()` for consistent spacing
- `Alignment.CenterHorizontally` for centered content
- `Arrangement.SpaceBetween` for vertical distribution in Player

---

## Accessibility

- All images have `contentDescription` (or null for decorative)
- Play/Pause button has descriptive content based on state
- High contrast text on dark overlays
- Touch targets: 80dp (button) exceeds 48dp minimum

---

## Assets Summary

| Asset Name | Type | Purpose | Size |
|------------|------|---------|------|
| `bg_splash.xml` | Drawable | Splash background | Full screen |
| `bg_player.xml` | Drawable | Player background | Full screen |
| `logo_blurfm.xml` | Drawable | App logo | 120-200dp |
| `cover_placeholder.xml` | Drawable | Album artwork | 280dp |
| `ic_play.xml` | Drawable | Play button | 40dp |
| `ic_pause.xml` | Drawable | Pause button | 40dp |

---

## Future Enhancements

- [ ] Replace placeholder drawables with actual photos
- [ ] Connect to streaming API
- [ ] Add live metadata updates
- [ ] Add loading states
- [ ] Add error handling
- [ ] Add media notifications
- [ ] Add background playback service
- [ ] Add network status indicator
- [ ] Add volume controls
- [ ] Add favorites/history
