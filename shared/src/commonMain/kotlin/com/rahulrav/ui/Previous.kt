package com.rahulrav.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rahulrav.diff.State as DiffState

@Composable
fun Previous(
    modifier: Modifier,
    states: List<DiffState>,
    boundsTransform: BoundsTransform,
    sharedScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    // In Previous, we show the _before_ state.
    // This means we only show deletes, and matches.
    Snippet(
        modifier = modifier,
        states = states,
        predicate = { state -> state is DiffState.Match || state is DiffState.Delete },
        lineSelector = { state ->
            when (state) {
                is DiffState.Match -> state.previous.lineNumber
                is DiffState.Delete -> state.token.lineNumber
                else -> throw IllegalStateException("Should never happen")
            }
        },
        tokenSelector = { state ->
            when (state) {
                // Pick the previous state for the start index
                is DiffState.Match -> state.previous.startIndex
                is DiffState.Delete -> state.token.startIndex
                else -> throw IllegalStateException("Should never happen")
            }
        },
        boundsTransform = boundsTransform,
        sharedScope = sharedScope,
        animatedVisibilityScope = animatedVisibilityScope,
    )
}
