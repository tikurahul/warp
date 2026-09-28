package com.rahulrav.diff

import androidx.collection.MutableObjectList
import androidx.collection.ScatterMap
import androidx.collection.mutableObjectListOf
import androidx.collection.mutableScatterMapOf
import com.rahulrav.parser.Token

// This is an adaptation of the paper:
// P. Heckel, A technique for isolating differences between files
//Comm. ACM, 21, (4), 264–268 (1978).

// This can help compute structural similarities between 2 text sequences.
// Once we know this, we should be able to animate cleanly between these 2 sequences.

fun diff(
    previous: List<Token>,
    current: List<Token>,
    /** Optionally pass in [ContentIds] to manage assignment of ids that are stable. */
    contentIds: ContentIds? = null
): List<State> {
    // Symbol tables
    val statesP: Array<State> = Array(previous.size) { State.Empty }
    val statesC: Array<State> = Array(current.size) { State.Empty }
    // Frequency + Context
    val (freqP, contextP) = frequencyAndContext(tokens = previous)
    val (freqC, _) = frequencyAndContext(tokens = current)
    // Phase 1: Find unique anchors such that frequency = 1 in both lists.
    // That is a match.
    // Additionally: Keep track of the matches in an ObjectList, so we can use
    // that in Phase 2, and 3.
    val matches = mutableObjectListOf<State.Match>()
    current.forEachIndexed { index, token ->
        val c = freqC[token]
        val p = freqP[token]
        val context = contextP[token]
        if (c == 1 && p == 1 && context != null) {
            // Here we are also keeping track of the previous token.
            // This is because we exclude the actual start, and end offsets from the equals()
            // to find high quality anchor points.
            val tokenP = context.token
            val match = State.Match(
                previous = tokenP,
                previousIdx = context.index,
                current = token,
                currentIdx = index
            )
            statesP[match.previousIdx] = match
            statesC[match.currentIdx] = match
            matches += match
        }
    }
    // Phase 2, 3: We found unique anchors.
    // Move forward and backwards, breadth first.
    // Move forward and find the ones that are matching adjacent to the ones that already matched.
    val steps = arrayOf(-1, 1)
    while (matches.isNotEmpty()) {
        // Use this like a stack.
        // Process the matches FIFO.
        val frontier = matches.removeAt(index = 0)
        for (step in steps) {
            val nc = frontier.currentIdx + step
            val np = frontier.previousIdx + step
            if (nc !in current.indices) continue
            if (np !in previous.indices) continue
            if (statesP[np] != State.Empty) continue
            if (statesC[nc] != State.Empty) continue
            if (previous[np] != current[nc]) continue
            val match = State.Match(
                previous = previous[np],
                previousIdx = np,
                current = current[nc],
                currentIdx = nc
            )
            statesP[np] = match
            statesC[nc] = match
            matches += match
            // Check for relationships
            val rp = match.previous.relatedIndex
            val rc = match.current.relatedIndex
            if (
                rp != null &&
                rc != null &&
                match.previous.related != null &&
                match.current.related != null &&
                statesP[rp] == State.Empty &&
                statesC[rc] == State.Empty
            ) {
                val related = State.Match(
                    previous = match.previous.related!!,
                    previousIdx = rp,
                    current = match.current.related!!,
                    currentIdx = rc
                )
                statesP[rp] = related
                statesC[rc] = related
                matches += related
            }
        }
    }
    // Phase 4: Final pass.
    // Anything that did not match in:
    // - statesP = Delete
    // - statesC = Insert
    for (i in statesP.indices) {
        if (statesP[i] == State.Empty) {
            statesP[i] = State.Delete(token = previous[i], index = i)
        }
    }
    val size = maxOf(statesP.size, statesC.size)
    // Keeps track of the last known deleted index in statesP
    var deleteIdx = 0
    // This is the final edit script.
    val edits = ArrayList<State>(size)
    for (i in 0 until size) {
        if (i < statesP.size && deleteIdx <= i && statesP[i] is State.Delete) {
            // We want to cluster all the deletes that occur next to each other.
            var j = i
            while (j < statesP.size && statesP[j] is State.Delete) {
                // Add deletions to the list of edits.
                edits += statesP[j]
                j += 1
            }
            deleteIdx = j
        }
        if (i < statesC.size) {
            val state = statesC[i]
            if (state is State.Match) {
                if (contentIds != null && state.previous.hasContentId()) {
                    // Propagate content ids for matched tokens
                    state.current.assignContentId(newContentId = state.previous.contentId())
                }
            }
            if (statesC[i] == State.Empty) {
                // Assign a new content id for newly inserted tokens
                contentIds?.assignContentId(current[i])
                statesC[i] = State.Insert(token = current[i], index = i)
            }
            edits += statesC[i]
        }
    }
    return edits
}

data class TokenContext(val token: Token, val index: Int)

/** The search limit when looking for matching tokens in the stack. */
internal const val SEARCH_LIMIT = 3

internal fun frequencyAndContext(
    tokens: List<Token>
): Pair<ScatterMap<Token, Int>, ScatterMap<Token, TokenContext?>> {
    val stack = MutableObjectList<TokenContext>()
    val freq = mutableScatterMapOf<Token, Int>()
    // Context for tokens whose count == 1
    val indexes = mutableScatterMapOf<Token, TokenContext?>()
    tokens.forEachIndexed { index, token ->
        relatedToken(token = token, index = index, stack = stack)
        val count = freq.getOrDefault(token, 0)
        if (count == 0) {
            indexes[token] = TokenContext(token = token, index = index)
        } else {
            // If this is not the first time we are seeing this
            // We no longer care about tracking its index.
            indexes[token] = null
        }
        freq[token] = count + 1
    }
    return freq to indexes
}

/**
 * Builds related tokens, when applicable. This is only ever populated for [Token]s that are
 * brackets. So `}`, `]`, `>`, `)` will point to their corresponding matching pairs. This helps
 * ensure that once we find a match for one of these [Token]s, we also match the corresponding
 * related tokens.
 */
internal fun relatedToken(token: Token, index: Int, stack: MutableObjectList<TokenContext>) {
    if (token.ignoreScope()) return
    if (token.isBegin()) {
        stack += TokenContext(token = token, index = index)
    } else if (token.isEnd()) {
        var i = 0
        while (i < SEARCH_LIMIT) {
            val stackIndex = stack.lastIndex - i
            // Check for out of bounds
            if (stackIndex < 0) break
            val candidate = stack[stackIndex]
            if (isMatching(begin = candidate.token, end = token)) {
                stack.removeAt(stackIndex)
                // Assign relationships to each other.
                token.assignRelated(candidate)
                candidate.token.assignRelated(context = TokenContext(index = index, token = token))
                break
            }
            i += 1
        }
    }
}

internal fun Token.ignoreScope(): Boolean {
    // Comments and String literals should be ignored
    // We don't need to find matching pairs for them.
    return when {
        scope == "comment" -> true
        scope.startsWith("comment.") -> true

        scope == "string" -> true
        scope.startsWith("string.") -> true

        else -> false
    }
}

internal fun isMatching(begin: Token, end: Token): Boolean {
    if (begin.content == "{" && end.content == "}") return true
    if (begin.content == "[" && end.content == "]") return true
    if (begin.content == "<" && end.content == ">") return true
    if (begin.content == "(" && end.content == ")") return true
    return false
}

internal fun Token.isBegin(): Boolean {
    return when (content) {
        "{" -> true
        "[" -> true
        "<" -> true
        "(" -> true
        else -> false
    }
}

internal fun Token.isEnd(): Boolean {
    return when (content) {
        "}" -> true
        "]" -> true
        ">" -> true
        ")" -> true
        else -> false
    }
}
