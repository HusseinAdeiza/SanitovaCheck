package com.sanitova.sanitovacheck

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.sanitova.sanitovacheck.ui.theme.*
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 8000L

@OptIn(UnstableApi::class)
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    var showFallback by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Try to set up ExoPlayer; fall back if video file is missing
    val exoPlayer = remember(context) {
        try {
            val rawId = context.resources.getIdentifier("splash_video", "raw", context.packageName)
            if (rawId == 0) {
                showFallback = true
                null
            } else {
                val player = ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(Uri.parse("android.resource://${context.packageName}/$rawId")))
                    prepare()
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_OFF
                }
                player
            }
        } catch (_: Exception) {
            showFallback = true
            null
        }
    }

    // Auto-advance timer (also drives progress bar)
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < SPLASH_DURATION_MS) {
            val elapsed = System.currentTimeMillis() - startTime
            progress = elapsed.toFloat() / SPLASH_DURATION_MS
            delay(50)
        }
        progress = 1f
        onFinished()
    }

    // Listen for video end
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    onFinished()
                }
            }
        }
        exoPlayer?.addListener(listener)
        onDispose {
            exoPlayer?.removeListener(listener)
            exoPlayer?.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepWater,
                        Color(0xFF0A2A3A),
                        Color(0xFF061E2A)
                    )
                )
            )
    ) {
        if (exoPlayer != null && !showFallback) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            SplashFallbackAnimation()
        }

        // Bottom progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.BottomCenter),
            color = OceanTealLight,
            trackColor = DeepWaterLight.copy(alpha = 0.3f),
        )

        // Skip button
        TextButton(
            onClick = onFinished,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                "Skip",
                color = PureWhite.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun SplashFallbackAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val ripple1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple1"
    )
    val ripple1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple1Alpha"
    )

    val ripple2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1000)
        ),
        label = "ripple2"
    )
    val ripple2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1000)
        ),
        label = "ripple2Alpha"
    )

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Ripple circles
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(ripple1)
                    .alpha(ripple1Alpha)
                    .background(OceanTeal.copy(alpha = 0.2f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(ripple2)
                    .alpha(ripple2Alpha)
                    .background(Aqua.copy(alpha = 0.15f), CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // App icon placeholder (shield/drop shape)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(logoScale)
                    .background(
                        Brush.radialGradient(
                            listOf(OceanTealLight, OceanTeal)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "💧",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PureWhite
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "SanitovaCheck",
                style = MaterialTheme.typography.headlineMedium,
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Protecting communities through WASH assessment",
                style = MaterialTheme.typography.bodyLarge,
                color = AquaLight,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Key stat card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DeepWaterLight.copy(alpha = 0.15f)
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "2 Billion",
                        style = MaterialTheme.typography.displaySmall,
                        color = OceanTealLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "people worldwide lack safe drinking water",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PureWhite.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "WASH saves lives. Every assessment matters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AquaLight.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            Text(
                "Loading...",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.5f)
            )
        }
    }
}
