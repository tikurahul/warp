package com.rahulrav.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
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

private const val FONT_SIZE = 48
private const val LINE_HEIGHT = 1.5 * FONT_SIZE

@Composable
internal fun Snippet(
    modifier: Modifier,
    states: List<DiffState>,
    predicate: (state: DiffState) -> Boolean,
    lineSelector: (state: DiffState) -> Int,
    tokenSelector: (state: DiffState) -> Int,
    // Control on how fast we move from old locations to new locations
    boundsTransform: BoundsTransform,
    sharedScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val rows: Map<Int, List<DiffState>> = remember(
        key1 = states,
        key2 = predicate,
        key3 = lineSelector
    ) {
        states.filter(predicate).groupBy { lineSelector(it) }.toSortedMap()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        rows.forEach { (_, states) ->
            Row {
                val sorted = states.sortedBy(selector = tokenSelector)
                sorted.forEach { state ->
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

private fun DiffState.Match.sharedKey(): ULong {
    return currentIdx.toULong().shl(bitCount = 32)
        .or(other = previousIdx.toULong() and 0xFFFFFFFFUL)
}
