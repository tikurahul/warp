package com.rahulrav.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.rahulrav.diff.State as DiffState

@Composable
internal fun Code(
    modifier: Modifier,
    slides: List<Slide>,
    idx: Int,
    sharedScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        val slide = remember(key1 = slides, key2 = idx) { slides[idx] }
        slide.rows.forEach { states ->
            Row {
                states.forEach { state ->
                    when (state) {
                        is DiffState.Match -> {
                            with(receiver = sharedScope) {
                                Text(
                                    text = state.text(),
                                    color = state.color(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = FONT_SIZE.sp,
                                    lineHeight = LINE_HEIGHT.sp,
                                    modifier = Modifier.sharedElement(
                                        sharedContentState = rememberSharedContentState(key = state.sharedKey()),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        boundsTransform = boundsTransform
                                    )
                                )
                            }
                        }

                        is DiffState.Insert -> {
                            with(sharedScope) {
                                with(animatedVisibilityScope) {
                                    Text(
                                        state.token.content,
                                        color = state.color(),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = FONT_SIZE.sp,
                                        lineHeight = LINE_HEIGHT.sp,
                                        modifier = Modifier
                                            .sharedElement(
                                                sharedContentState = rememberSharedContentState(key = state.token.contentId()),
                                                animatedVisibilityScope = animatedVisibilityScope,
                                                boundsTransform = boundsTransform
                                            )
                                            .renderInSharedTransitionScopeOverlay()
                                            .animateEnterExit(
                                                enter = fadeIn(animationSpec = tween(durationMillis = TWEEN_DURATION_MS)),
                                                exit = fadeOut(animationSpec = tween(durationMillis = TWEEN_DURATION_MS))
                                            )
                                    )
                                }
                            }
                        }

                        is DiffState.Delete -> {
                            with(sharedScope) {
                                with(animatedVisibilityScope) {
                                    Text(
                                        state.token.content,
                                        color = state.color(),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = FONT_SIZE.sp,
                                        lineHeight = LINE_HEIGHT.sp,
                                        modifier = Modifier
                                            .sharedElement(
                                                sharedContentState = rememberSharedContentState(key = state.token.contentId()),
                                                animatedVisibilityScope = animatedVisibilityScope,
                                                boundsTransform = boundsTransform
                                            )
                                            .renderInSharedTransitionScopeOverlay()
                                            .animateEnterExit(
                                                enter = fadeIn(animationSpec = tween(durationMillis = TWEEN_DURATION_MS)),
                                                exit = fadeOut(animationSpec = tween(durationMillis = TWEEN_DURATION_MS))
                                            )
                                    )
                                }
                            }
                        }

                        else -> {
                            // Should never really happen
                        }
                    }
                }
            }
        }
    }
}

internal fun DiffState.Match.sharedKey(): String {
    return if (previous.hasContentId() && current.hasContentId()) {
        check(previous.contentId() == current.contentId()) {
            "Content Id must match for $previous and $current"
        }
        current.contentId()
    } else {
        // The next best thing is to return an identifier that is automatically unique
        // between 2 slides (but not across a deck).
        "Match('$previousIdx' -> '$currentIdx')"
    }
}
