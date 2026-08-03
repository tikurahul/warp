package com.rahulrav.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

const val TWEEN_DURATION_MS = 200 // Milliseconds
val boundsTransform = BoundsTransform { _, _ ->
    spring(
        dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
    )
}

@Composable
fun Scaffold() {
    val hScrollState = rememberScrollState()
    val vScrollState = rememberScrollState()
    val presentation: List<Slide> = remember {
        buildPresentation(contents = TRACING_SLIDES)
    }
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(), color = Background
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(128.dp).verticalScroll(vScrollState)
                    .horizontalScroll(hScrollState)
            ) {
                Warp(slides = presentation, modifier = Modifier)
            }
        }
    }
}

@Composable
fun Warp(
    slides: List<Slide>, modifier: Modifier
) {
    var slideIdx by mutableIntStateOf(0)
    val autoPlay by remember { mutableStateOf(true) }
    SharedTransitionLayout(modifier = modifier) {
        AnimatedContent(
            targetState = slideIdx, transitionSpec = {
                fadeIn(
                    animationSpec = tween(durationMillis = TWEEN_DURATION_MS)
                ).togetherWith(
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = TWEEN_DURATION_MS)
                    )
                )
            }, label = "WarpTransition"
        ) { targetIdx ->
            Code(
                modifier = modifier,
                slides = slides,
                idx = targetIdx,
                sharedScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this@AnimatedContent
            )
        }
    }
    LaunchedEffect(autoPlay) {
        // Keep replaying animation
        if (autoPlay) {
            val size = slides.size
            while (true) {
                delay(2.seconds)
                var idx = slideIdx
                idx += 1
                slideIdx = idx % size
            }
        }
    }
}
