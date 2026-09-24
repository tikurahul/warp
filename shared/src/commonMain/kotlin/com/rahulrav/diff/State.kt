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
            // Picks the match that is closer.
            val indexPenalty = abs(current.startIndex - previous.startIndex)
            val linePenalty = abs(current.lineNumber - previous.lineNumber)
            return indexPenalty + linePenalty
        }
    }

    /** Represents an insert. */
    data class Insert(val token: Token, val index: Int) : State()

    /** Represents a deletion. */
    data class Delete(val token: Token, val index: Int) : State()
}
