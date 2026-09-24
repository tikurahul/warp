package com.rahulrav.diff

import com.rahulrav.parser.Token
import kotlin.math.abs

sealed class State {
    /** Represents the initial state. */
    object Empty : State()

    /** This represents a Match. */
    data class Match(
        val previous: Token,
        val current: Token,
    ) : State() {
        fun text() = current.content
        val previousIdx = previous.index
        val currentIdx = current.index
        fun penalty(): Int {
            val indexDelta = abs(current.startIndex - previous.startIndex)
            val lineDelta = abs(current.lineNumber - previous.lineNumber)
            val depthDelta = abs(current.depth - previous.depth)
            // Scale penalties.
            // Prefer matches in the same scope if possible. Apply a heavy penalty when we move
            // scope boundaries.
            // Line moves have quadratic penalty.
            // Index moves are the cheapest.
            val depthPenalty = depthDelta * 1000_000
            val linePenalty = lineDelta * lineDelta * 100
            return indexDelta + linePenalty + depthPenalty
        }
    }

    /** Represents an insert. */
    data class Insert(val token: Token, val index: Int) : State()

    /** Represents a deletion. */
    data class Delete(val token: Token, val index: Int) : State()
}
