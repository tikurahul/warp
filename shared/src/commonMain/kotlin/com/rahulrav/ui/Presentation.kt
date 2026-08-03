package com.rahulrav.ui

import com.rahulrav.diff.State
import com.rahulrav.diff.diff
import com.rahulrav.parser.parseKotlin

/** The slide. */
data class Slide(val rows: List<List<State>>)

fun buildPresentation(contents: List<String>): List<Slide> {
    if (contents.isEmpty()) return emptyList()
    val deck = mutableListOf<Slide>()
    val slides = contents.map { slide -> parseKotlin(code = slide) }
    val slidePairs = slides.zipWithNext()
    slidePairs.forEach { (previous, current) ->
        val states = diff(previous, current)
        deck += previousSlide(states)
        deck += currentSlide(states)
    }
    return deck
}

// This does not really need to be a Map, rather wasteful
// Lots of room to optimize here.
internal fun currentSlide(states: List<State>): Slide {
    // In Current, we show the _after_ state.
    // This means we only show inserts, and matches.
    val filtered = states.filter { state -> state is State.Match || state is State.Insert }
    // Group by the line
    val rows: Map<Int, List<State>> = filtered.groupBy { state ->
        when (state) {
            is State.Match -> state.current.lineNumber
            is State.Insert -> state.token.lineNumber
            else -> throw IllegalStateException("Should never happen")
        }
    }
    // Sort by the startIndex so they are ordered correctly
    val sortedArray: Array<List<State>?> = arrayOfNulls(size = rows.size)
    rows.forEach { (rowIdx, states) ->
        val tokens = states.sortedBy { state ->
            when (state) {
                // Pick the previous state for the start index
                is State.Match -> state.current.startIndex
                is State.Insert -> state.token.startIndex
                else -> throw IllegalStateException("Should never happen")
            }
        }
        sortedArray[rowIdx] = tokens
    }
    @Suppress("UNCHECKED_CAST")
    return Slide(rows = (sortedArray as Array<List<State>>).asList())
}

internal fun previousSlide(states: List<State>): Slide {
    // In Previous, we show the _before_ state.
    // This means we only show deletes, and matches.
    val filtered = states.filter { state -> state is State.Match || state is State.Delete }
    // Group by the line
    val rows: Map<Int, List<State>> = filtered.groupBy { state ->
        when (state) {
            is State.Match -> state.previous.lineNumber
            is State.Delete -> state.token.lineNumber
            else -> throw IllegalStateException("Should never happen")
        }
    }
    // Sort by the startIndex so they are ordered correctly
    val sortedArray: Array<List<State>?> = arrayOfNulls(size = rows.size)
    rows.forEach { (rowIdx, states) ->
        val tokens = states.sortedBy { state ->
            when (state) {
                // Pick the previous state for the start index
                is State.Match -> state.previous.startIndex
                is State.Delete -> state.token.startIndex
                else -> throw IllegalStateException("Should never happen")
            }
        }
        sortedArray[rowIdx] = tokens
    }
    @Suppress("UNCHECKED_CAST")
    return Slide(rows = (sortedArray as Array<List<State>>).asList())
}
