package com.callcenter.kidcare.ui.uionly

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.imageLoader
import com.callcenter.kidcare.R
import com.callcenter.kidcare.ui.theme.particle.ParticleAnimation
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var isVisible by remember { mutableStateOf(true) }
    LocalContext.current

    val transition = updateTransition(targetState = isVisible, label = "SplashTransition")

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) },
        label = "ScaleAnimation"
    ) { state -> if (state) 1f else 0.8f }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) },
        label = "AlphaAnimation"
    ) { state -> if (state) 1f else 0f }

    LaunchedEffect(Unit) {
        delay(3000)
        isVisible = false
        delay(500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            ),
        contentAlignment = Alignment.Center
    ) {
        val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels
        val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels

        ParticleAnimation(screenWidth, screenHeight)
        Image(
            painter = rememberAsyncImagePainter(
                model = R.drawable.logo_sidokter_login_screen_animation,
                imageLoader = LocalContext.current.imageLoader.newBuilder()
                    .components {
                        add(GifDecoder.Factory())
                    }
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier.size(280.dp)
        )
    }
}
