package com.rahulrav.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rahulrav.diff.State as DiffState

@Composable
fun Current(
    modifier: Modifier,
    states: List<DiffState>,
    boundsTransform: BoundsTransform,
    sharedScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    // In Current, we show the _after_ state.
    // This means we only show inserts, and matches.
    Snippet(
        modifier = modifier,
        states = states,
        predicate = { state -> state is DiffState.Match || state is DiffState.Insert },
        lineSelector = { state ->
            when (state) {
                is DiffState.Match -> state.current.lineNumber
                is DiffState.Insert -> state.token.lineNumber
                else -> throw IllegalStateException("Should never happen")
            }
        },
        tokenSelector = { state ->
            when (state) {
                // Pick the previous state for the start index
                is DiffState.Match -> state.current.startIndex
                is DiffState.Insert -> state.token.startIndex
                else -> throw IllegalStateException("Should never happen")
            }
        },
        boundsTransform = boundsTransform,
        sharedScope = sharedScope,
        animatedVisibilityScope = animatedVisibilityScope,
    )
}
