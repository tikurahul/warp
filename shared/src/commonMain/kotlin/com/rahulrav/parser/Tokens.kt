package com.rahulrav.parser

import androidx.collection.MutableObjectList

/** The search limit when looking for matching tokens in the stack. */
private const val SEARCH_LIMIT = 3

/**
 * Builds related tokens, when applicable. This is only ever populated for [Token]s that are
 * brackets. So `}`, `]`, `>`, `)` will point to their corresponding matching pairs. This helps
 * ensure that once we find a match for one of these [Token]s, we also match the corresponding
 * related tokens.
 */
fun buildRelatedTokens(tokens: List<Token>) {
    val stack = MutableObjectList<Token>()
    for (index in tokens.indices) {
        val token = tokens[index]
        // Assign an index
        token.assignIndex(index)
        if (token.ignoreScope()) continue
        // Let's attempt to find matching pairs.
        if (token.isBegin()) {
            stack += token
        } else if (token.isEnd()) {
            var i = 0
            while (i < SEARCH_LIMIT) {
                val index = stack.lastIndex - i
                // Check for out of bounds
                if (index < 0) break
                val begin = stack.removeAt(index)
                if (isMatching(begin = begin, end = token)) {
                    token.assignRelated(begin)
                    break
                }
                i += 1
            }
        }
    }
}

private fun Token.ignoreScope(): Boolean {
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

private fun isMatching(begin: Token, end: Token): Boolean {
    if (begin.content == "{" && end.content == "}") return true
    if (begin.content == "[" && end.content == "]") return true
    if (begin.content == "<" && end.content == ">") return true
    if (begin.content == "(" && end.content == ")") return true
    return false
}

private fun Token.isBegin(): Boolean {
    return when (content) {
        "{" -> true
        "[" -> true
        "<" -> true
        "(" -> true
        else -> false
    }
}

private fun Token.isEnd(): Boolean {
    return when (content) {
        "}" -> true
        "]" -> true
        ">" -> true
        ")" -> true
        else -> false
    }
}
