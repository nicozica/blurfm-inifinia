package com.argensonix.blurfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.argensonix.blurfm.ui.theme.BlurFMTheme
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlurFmApp()
        }
    }
}

/**
 * Main app composable with navigation setup.
 * Handles navigation between splash screen and player screen.
 */
@Composable
fun BlurFmApp() {
    BlurFMTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "splash",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable("splash") {
                SplashScreen(onTimeout = {
                    navController.navigate("player") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }
            composable("player") {
                PlayerScreen()
            }
        }
    }
}

/**
 * Splash screen displayed on app launch.
 * Shows the Blur FM logo with background image and navigates to player after 3 seconds.
 *
 * @param onTimeout Callback invoked after the splash duration completes
 */
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Navigate to player screen after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.bg_splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark blue overlay for better logo visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC001F3F))
        )

        // Logo centered near bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_blurfm),
                contentDescription = "Blur FM Logo",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}

/**
 * Main player screen with ExoPlayer integration.
 * Handles audio streaming, playback controls, and error states.
 */
@Composable
fun PlayerScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for tracking playback status
    var isPlaying by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    // Initialize ExoPlayer with proper lifecycle management
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Prepare the media source with the stream URL
            val mediaItem = MediaItem.fromUri(StreamConfig.STREAM_URL)
            setMediaItem(mediaItem)
            prepare()

            // Listen to player state changes to update UI
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            hasError = false
                        }
                        Player.STATE_ENDED -> {
                            isPlaying = false
                        }
                    }
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }

                override fun onPlayerError(error: PlaybackException) {
                    hasError = true
                    isPlaying = false

                    // Show error message to user
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Error loading stream: ${error.message ?: "Unknown error"}",
                            actionLabel = "Retry"
                        )
                    }
                }
            })
        }
    }

    // Release player resources when composable leaves composition
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.bg_player),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark overlay for better text contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x88000000))
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_blurfm),
                    contentDescription = "Blur FM Logo",
                    modifier = Modifier.size(120.dp)
                )
            }

            // Middle section with album cover and track info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Album cover card with shadow
                Card(
                    modifier = Modifier.size(280.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cover_placeholder),
                        contentDescription = "Album Cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Track title
                Text(
                    text = "Un Deux Trois",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Artist name
                Text(
                    text = "MUNYA",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom section with play/pause button
            Box(
                modifier = Modifier.padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        // Toggle playback state
                        if (isPlaying) {
                            exoPlayer.pause()
                        } else {
                            if (hasError) {
                                // Retry loading stream if there was an error
                                exoPlayer.prepare()
                                hasError = false
                            }
                            exoPlayer.play()
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                        ),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Snackbar for error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

