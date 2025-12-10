package com.argensonix.blurfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.argensonix.blurfm.ui.theme.BlurFMTheme
import com.argensonix.blurfm.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay


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
 * Show the full bg_splash image as-is (it already contains logo and color treatment).
 * Remove any extra overlay or duplicated logo so the asset's original colors are preserved.
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
        // Show the splash background image full-screen and crop to fill the viewport.
        // English comment: Use the provided asset as-is; it already includes the logo and color treatment.
        Image(
            painter = painterResource(id = R.drawable.bg_splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // NOTE: No overlay and no extra logo drawn here. The provided asset includes the logo and color treatment.
    }
}

/**
 * Main player screen with ExoPlayer integration.
 * Handles audio streaming, playback controls, error states, and now playing information.
 */
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect state from ViewModel
    val isPlaying by viewModel.isPlaying.collectAsState()
    val hasError by viewModel.hasError.collectAsState()
    val nowPlaying by viewModel.nowPlaying.collectAsState()

    // Show error message when stream fails
    LaunchedEffect(hasError) {
        if (hasError) {
            val result = snackbarHostState.showSnackbar(
                message = "Error loading stream",
                actionLabel = "Retry"
            )
            if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                viewModel.retry()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image for the player: fills entire viewport and is cropped to avoid distortion
        // English comment: Use the background asset full-screen with ContentScale.Crop so it always covers the viewport.
        Image(
            painter = painterResource(id = R.drawable.bg_player),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(), // ensure it occupies the full viewport
            contentScale = ContentScale.Crop // crop to fill without distortion
        )

        // Small debug refresh control (debug only) so you can manually trigger now playing refresh.
        // English comment: This is visible only in debug builds to help testing; it calls viewModel.refreshNowPlaying().
        if (BuildConfig.DEBUG) {
            androidx.compose.material3.Text(
                text = "Refresh",
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clickable { viewModel.refreshNowPlaying() }
            )
        }

        // NOTE: Removed dark overlay here so the background image colors remain intact (per request).

        // Main content stacked above the background
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with small logo
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
                // Album cover card with dynamic artwork from iTunes or Blur FM logo as fallback
                Card(
                    modifier = Modifier.size(280.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    // English comment: Use a themed container color (not bright white) and rounded corners so the placeholder is visible on top of the background.
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Treat blank artwork URLs as missing and use a proper placeholder drawable.
                        // English comment: Ensure we don't try to load empty strings as URLs; fall back to cover_placeholder.
                        val artworkUrl = nowPlaying.artworkUrl?.takeIf { it.isNotBlank() }

                        if (artworkUrl != null) {
                            // Display album artwork from iTunes
                            AsyncImage(
                                model = artworkUrl,
                                contentDescription = "Album Cover",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.cover_placeholder),
                                error = painterResource(id = R.drawable.cover_placeholder)
                            )
                        } else {
                            // Display cover placeholder image (use cover_placeholder which is specific for album art)
                            // English comment: Center the placeholder with padding so it remains visible on top of similarly colored backgrounds.
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.cover_placeholder),
                                    contentDescription = "Cover placeholder",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Track title - dynamic from now playing
                Text(
                    text = nowPlaying.title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Artist name - dynamic from now playing
                Text(
                    text = nowPlaying.artist,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                // DEBUG: show raw nowPlaying values to help diagnose missing metadata/cover
                // English comment: Temporary debug info - remove once troubleshooting is complete.
                val debugShow = BuildConfig.DEBUG
                if (debugShow) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "DEBUG: title='${nowPlaying.title}' artist='${nowPlaying.artist}' artwork='${nowPlaying.artworkUrl ?: "null"}'",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Bottom section with play/pause button
            Box(
                modifier = Modifier.padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                // Simplified circular touch target: do not draw an extra solid circle behind the drawable
                // English comment: Keep the button surface transparent to avoid a "double circle" effect; rely on the drawable's appearance.
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .clickable {
                            // Toggle playback through ViewModel (handles fade-in and stream reconnection)
                            viewModel.togglePlayPause()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Use a smaller icon size so the drawable has room and looks centered inside the touch target
                    Image(
                        painter = painterResource(
                            id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                        ),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(44.dp),
                        contentScale = ContentScale.Fit
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
