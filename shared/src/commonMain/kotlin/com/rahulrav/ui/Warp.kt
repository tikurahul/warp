package com.rahulrav.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahulrav.diff.diff
import com.rahulrav.parser.parseKotlin
import kotlinx.coroutines.delay
import org.intellij.lang.annotations.Language
import kotlin.time.Duration.Companion.seconds
import com.rahulrav.diff.State as DiffState

// Magic Move
// Define previous and current states

@Language("kotlin")
val slideP = """
  val x = 10
""".trimIndent()

@Language("kotlin")
val slideC = """
  val x = 20
""".trimIndent()

// The default bounds transform
val boundsTransform = BoundsTransform { _, _ ->
    tween(durationMillis = 250)
}

@Composable
fun Scaffold() {
    val hScrollState = rememberScrollState()
    val vScrollState = rememberScrollState()
    var isCurrent by remember { mutableStateOf(false) }
    val autoPlay by remember { mutableStateOf(true) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(128.dp)
                    .verticalScroll(vScrollState)
                    .horizontalScroll(hScrollState)
            ) {
                Warp(modifier = Modifier, isCurrent = isCurrent)
            }
        }
    }
    LaunchedEffect(autoPlay) {
        // Keep replaying animation
        if (autoPlay) {
            while (true) {
                delay(1.seconds)
                isCurrent = !isCurrent
            }
        }
    }
}

@Composable
fun Warp(modifier: Modifier, isCurrent: Boolean) {
    val states: List<DiffState> = remember(
        key1 = slideP,
        key2 = slideC
    ) {
        val previous = parseKotlin(code = slideP)
        val current = parseKotlin(code = slideC)
        diff(previous = previous, current = current)
    }
    SharedTransitionLayout(modifier = modifier) {
        AnimatedContent(targetState = isCurrent, label = "WarpTransition") { current ->
            if (current) {
                // Show the current slide
                Current(
                    modifier = modifier,
                    states = states,
                    boundsTransform = boundsTransform,
                    sharedScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            } else {
                // Show the previous slide
                Previous(
                    modifier = modifier,
                    states = states,
                    boundsTransform = boundsTransform,
                    sharedScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            }
        }
    }
}
