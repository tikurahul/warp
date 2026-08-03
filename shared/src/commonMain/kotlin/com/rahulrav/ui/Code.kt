package com.rahulrav.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
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
                            // Should automatically fade-in
                            Text(
                                state.token.content,
                                color = state.color(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = FONT_SIZE.sp,
                                lineHeight = LINE_HEIGHT.sp,
                            )
                        }

                        is DiffState.Delete -> {
                            // Should automatically fade-out
                            Text(
                                state.token.content,
                                color = state.color(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = FONT_SIZE.sp,
                                lineHeight = LINE_HEIGHT.sp,
                            )
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

internal fun DiffState.Match.sharedKey(): ULong {
    return currentIdx.toULong().shl(bitCount = 32)
        .or(other = previousIdx.toULong() and 0xFFFFFFFFUL)
}
