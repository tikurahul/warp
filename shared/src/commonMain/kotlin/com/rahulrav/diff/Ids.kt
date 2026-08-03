package com.rahulrav.diff

import androidx.collection.mutableScatterMapOf
import com.rahulrav.parser.Token

class ContentIds {
    /** The stable content ids for all the tokens in a presentation. */
    internal val idMap = mutableScatterMapOf<Token, Int>()

    /**
     * Assigns a new stable `contentId` for a given [Token] instance.
     */
    fun assignContentId(token: Token) {
        var count = idMap[token] ?: 0
        count += 1
        val id = "$token#$count"
        token.assignContentId(newContentId = id)
        idMap[token] = count
    }
}
