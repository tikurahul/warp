package com.rahulrav.diff

import com.rahulrav.parser.Token

sealed class State {
    /** Represents the initial state. */
    object Empty : State()

    /** This represents a Match. */
    data class Match(
        val previous: Token,
        val previousIdx: Int,
        val current: Token,
        val currentIdx: Int
    ) : State() {
        fun text() = current.content
    }

    /** Represents an insert. */
    data class Insert(val token: Token, val index: Int) : State()

    /** Represents a deletion. */
    data class Delete(val token: Token, val index: Int) : State()
}
