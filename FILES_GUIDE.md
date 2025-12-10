# 🗂️ Guía Rápida de Archivos - Blur FM

## 📱 MainActivity.kt (303 líneas)
**Qué hace:** Pantalla principal de la app con UI de Compose

**Features clave:**
- ✅ Integra ViewModel para estado reactivo
- ✅ Muestra título y artista dinámicos (nowPlaying.title/artist)
- ✅ Carga carátulas con AsyncImage de Coil
- ✅ Botón Play/Pause conectado al ViewModel
- ✅ Snackbar para errores con botón Retry
- ✅ Splash screen (3 segundos) → Player screen

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/MainActivity.kt
```

---

## 🎵 AudioPlayerManager.kt (154 líneas)
**Qué hace:** Controla ExoPlayer con fade-in y manejo de stream

**Features clave:**
- ✅ Fade-in de 800ms con ValueAnimator
- ✅ Reconexión al stream en vivo (no buffer)
- ✅ Stop completo al pausar (libera recursos)
- ✅ StateFlow para estado reactivo (isPlaying, hasError)
- ✅ Retry automático en errores
- ✅ Lifecycle management correcto

**Métodos principales:**
- `play()` - Inicia con fade-in
- `pause()`/`stop()` - Detiene y limpia buffer
- `togglePlayPause()` - Alterna estados
- `retry()` - Reconecta después de error

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/player/AudioPlayerManager.kt
```

---

## 🧠 PlayerViewModel.kt (143 líneas)
**Qué hace:** Gestiona estado y lógica de negocio (MVVM)

**Features clave:**
- ✅ Expone estado a la UI con StateFlow
- ✅ Actualiza now playing cada 20 segundos
- ✅ Coordina AudioPlayerManager y Repository
- ✅ Maneja errores sin crashear
- ✅ Lifecycle-aware (limpia recursos en onCleared)

**Estado expuesto:**
- `isPlaying: StateFlow<Boolean>` - ¿Está reproduciendo?
- `hasError: StateFlow<Boolean>` - ¿Hay error?
- `nowPlaying: StateFlow<NowPlaying>` - Info de la canción

**Métodos públicos:**
- `togglePlayPause()` - UI lo llama al tocar botón
- `retry()` - Reintentar después de error
- `refreshNowPlaying()` - Actualización manual (opcional)

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/ui/viewmodel/PlayerViewModel.kt
```

---

## 📊 NowPlayingRepository.kt (75 líneas)
**Qué hace:** Coordina datos de Now Playing API + iTunes

**Features clave:**
- ✅ Obtiene título/artista de tu API
- ✅ Busca carátula en iTunes basado en título+artista
- ✅ Escala artwork a 600x600 (alta resolución)
- ✅ Maneja errores sin romper la app
- ✅ Usa Coroutines (no bloquea UI)

**Métodos:**
- `fetchNowPlaying()` - Obtiene info básica de tu API
- `fetchArtwork()` - Busca carátula en iTunes
- `fetchCompleteNowPlaying()` - Combina ambos

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/data/repository/NowPlayingRepository.kt
```

---

## ⚙️ ApiConfig.kt (57 líneas)
**Qué hace:** Configura Retrofit para APIs

**⚠️ ESTE ES EL QUE DEBES EDITAR:**

**Línea 24:**
```kotlin
private const val NOW_PLAYING_BASE_URL = "https://api.blurfm.com/"
```
👆 **Cambiar esto a tu URL real**

**Features:**
- ✅ Configura iTunes API (ya lista)
- ✅ Configura tu Now Playing API (necesita URL)
- ✅ Logging para debugging
- ✅ Timeouts de 30 segundos

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/data/api/ApiConfig.kt
```

---

## 🎨 ITunesApiService.kt (27 líneas)
**Qué hace:** Define el endpoint de iTunes

**Features:**
- ✅ Busca canciones en iTunes
- ✅ Parámetros: term (artista+título), media, limit
- ✅ Devuelve ITunesSearchResponse

**Endpoint usado:**
```
GET https://itunes.apple.com/search?term=ARTISTA+TITULO&media=music&limit=1
```

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/data/api/ITunesApiService.kt
```

---

## 📡 NowPlayingApiService.kt (16 líneas)
**Qué hace:** Define el endpoint de tu Now Playing API

**Features:**
- ✅ Obtiene info de la canción actual
- ✅ Endpoint: `/nowplaying` (configurable)

**⚠️ Si tu endpoint es diferente, edita la ruta aquí:**
```kotlin
@GET("tu-ruta-personalizada")  // cambiar "nowplaying"
```

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/data/api/NowPlayingApiService.kt
```

---

## 📦 NowPlaying.kt (15 líneas)
**Qué hace:** Modelo de datos para la canción actual

**Estructura:**
```kotlin
data class NowPlaying(
    val title: String = "Blur FM",
    val artist: String = "Loading...",
    val artworkUrl: String? = null
)
```

**Usado en:**
- ViewModel (estado)
- Repository (construcción)
- UI (display)

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/data/model/NowPlaying.kt
```

---

## 🎵 NowPlayingModels.kt (15 líneas)
**Qué hace:** Define formato de respuesta de tu API

**⚠️ EDITAR SI TU API USA OTROS NOMBRES:**

**Actual:**
```kotlin
data class NowPlayingResponse(
    @SerializedName("title")
    val title: String?,
    @SerializedName("artist")
    val artist: String?
)
```

**Si tu API devuelve:**
```json
{
  "song_name": "Un Deux Trois",
  "artist_name": "MUNYA"
}
```

**Cambiar a:**
```kotlin
@SerializedName("song_name")
val title: String?,
@SerializedName("artist_name")
val artist: String?
```

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/data/api/NowPlayingModels.kt
```

---

## 🍎 ITunesModels.kt (33 líneas)
**Qué hace:** Define formato de respuesta de iTunes

**Estructura:**
```kotlin
data class ITunesSearchResponse(
    val resultCount: Int,
    val results: List<ITunesTrack>
)

data class ITunesTrack(
    val trackName: String?,
    val artistName: String?,
    val artworkUrl100: String?  // Se escala a 600x600
)
```

**No necesitas editar este archivo** (iTunes API es estándar)

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/data/api/ITunesModels.kt
```

---

## 🔧 StreamConfig.kt (24 líneas)
**Qué hace:** Configura la URL del stream de radio

**Contenido:**
```kotlin
object StreamConfig {
    const val STREAM_URL = "https://live.radiovague.com:8443/blurfm01"
    const val CONNECTION_TIMEOUT_MS = 10000
    const val BUFFER_DURATION_MS = 5000
}
```

**⚠️ Si tu stream URL cambió, editarlo aquí**

**Dónde está:**
```
app/src/main/java/com/argensonix/blurfm/StreamConfig.kt
```

---

## 📝 build.gradle.kts & libs.versions.toml
**Qué hacen:** Configuran dependencias del proyecto

**Dependencias agregadas:**
- Retrofit 2.9.0 (HTTP client)
- Gson Converter (JSON)
- OkHttp 4.12.0 (Networking)
- Coil 2.5.0 (Image loading)
- ViewModel Compose 2.6.1 (State management)

**Ya configuradas correctamente ✅**

**Dónde están:**
```
app/build.gradle.kts
gradle/libs.versions.toml
```

---

## 📚 Archivos de Documentación

### IMPLEMENTATION_COMPLETE.md
**Qué contiene:**
- Resumen técnico completo
- Detalles de implementación
- Guía de testing
- Troubleshooting

### CONFIGURATION_GUIDE.md
**Qué contiene:**
- Setup rápido paso a paso
- Ejemplos de diferentes formatos API
- Opciones de customización
- Debugging tips

### VERIFICATION_CHECKLIST.md
**Qué contiene:**
- Checklist completo de verificación
- Estado de cada feature
- Issues conocidos
- Próximos pasos

### check-implementation.sh
**Qué hace:**
- Script bash para verificar todo automáticamente
- Chequea archivos, dependencias, features
- Output con colores
- **Usar:** `./check-implementation.sh`

---

## 🎯 Flujo de Datos Simplificado

```
1. Usuario toca Play
   └─> MainActivity llama viewModel.togglePlayPause()
       └─> ViewModel llama audioPlayerManager.play()
           └─> AudioPlayerManager:
               - Crea ExoPlayer
               - Conecta a StreamConfig.STREAM_URL
               - Fade-in 0→1 en 800ms
               - Emite isPlaying = true
                   └─> UI actualiza icono a Pause

2. Cada 20 segundos (automático)
   └─> ViewModel llama repository.fetchCompleteNowPlaying()
       └─> Repository:
           1. Llama nowPlayingApi.getNowPlaying()
              └─> GET https://TU-API.com/nowplaying
                  └─> Obtiene { title, artist }
           
           2. Llama iTunesApi.searchTrack("artist title")
              └─> GET https://itunes.apple.com/search?term=...
                  └─> Obtiene artworkUrl100
                  └─> Escala a 600x600
           
           3. Retorna NowPlaying(title, artist, artworkUrl)
               └─> ViewModel emite nowPlaying StateFlow
                   └─> UI actualiza:
                       - Text con title
                       - Text con artist
                       - AsyncImage con artworkUrl

3. Usuario toca Pause
   └─> MainActivity llama viewModel.togglePlayPause()
       └─> ViewModel llama audioPlayerManager.pause()
           └─> AudioPlayerManager:
               - Cancela fade animator
               - Llama player.stop()
               - Emite isPlaying = false
                   └─> UI actualiza icono a Play
```

---

## 🔍 ¿Dónde Está Cada Feature?

| Feature | Archivo Principal | Líneas Clave |
|---------|------------------|--------------|
| Fade-in de audio | AudioPlayerManager.kt | 97-107 |
| Reconexión en vivo | AudioPlayerManager.kt | 82-89 |
| Stop y limpieza buffer | AudioPlayerManager.kt | 118-130 |
| Actualización cada 20s | PlayerViewModel.kt | 46-54 |
| Fetch Now Playing | NowPlayingRepository.kt | 24-40 |
| Fetch Artwork iTunes | NowPlayingRepository.kt | 50-65 |
| UI con ViewModel | MainActivity.kt | 130-145 |
| Carátula dinámica | MainActivity.kt | 205-228 |
| Texto dinámico | MainActivity.kt | 233-256 |
| Botón Play/Pause | MainActivity.kt | 264-281 |

---

## 🛠️ Customización Rápida

### Cambiar duración del fade-in (800ms → 1200ms):
**Archivo:** AudioPlayerManager.kt, línea 23
```kotlin
private const val FADE_IN_DURATION_MS = 1200L
```

### Cambiar frecuencia de actualización (20s → 15s):
**Archivo:** PlayerViewModel.kt, línea 39
```kotlin
private const val NOW_PLAYING_UPDATE_INTERVAL_MS = 15000L
```

### Cambiar URL del stream:
**Archivo:** StreamConfig.kt, línea 12
```kotlin
const val STREAM_URL = "https://tu-stream.com/radio"
```

### Cambiar endpoint de Now Playing:
**Archivo:** ApiConfig.kt, línea 24
```kotlin
private const val NOW_PLAYING_BASE_URL = "https://tu-api.com/"
```

### Cambiar ruta del endpoint:
**Archivo:** NowPlayingApiService.kt, línea 13
```kotlin
@GET("tu-ruta")  // default: "nowplaying"
```

---

## 🎓 Para Entender el Código

Si quieres entender cómo funciona todo:

1. **Empieza por:** MainActivity.kt (línea 130) - PlayerScreen()
   - Verás cómo se usa el ViewModel

2. **Luego ve a:** PlayerViewModel.kt (línea 20)
   - Verás cómo se gestiona el estado

3. **Después:** AudioPlayerManager.kt (línea 70)
   - Verás cómo se controla el audio

4. **Y finalmente:** NowPlayingRepository.kt (línea 67)
   - Verás cómo se obtienen los datos

---

**Resumen:** Todo está en su lugar, bien organizado, y listo para configurar y usar. Solo necesitas poner tu URL de API y compilar.

