package com.rahulrav.parser

/**
 * The [Token] that will be passed to the diffing algorithm to find structural similarities.
 *
 * We are using a combination of the [content], and the `primary` [scope] + its [depth] to find
 * `anchor`s.
 */
class Token(
    /** The actual content of the parsed token. */
    val content: String,
    /** The primary scope */
    val scope: String,
    /** The depth of the primary scope. */
    val depth: Int,
    /* More context for animations. */
    val lineNumber: Int,
    val startIndex: Int,
    val endIndex: Int
) {
    /** The underlying content id that was assigned to the token.
     * This is guaranteed to be stable across a deck. */
    private var contentId: String? = null

    fun hasContentId(): Boolean {
        return contentId != null
    }

    fun assignContentId(newContentId: String) {
        val contentId = contentId
        check(contentId == null) { "Cannot override ContentId for $this" }
        this.contentId = newContentId
    }

    fun contentId(): String {
        val contentId = contentId
        check(contentId != null) { "Content Id was not assigned to $this" }
        return contentId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Token

        if (depth != other.depth) return false
        if (content != other.content) return false
        if (scope != other.scope) return false

        return true
    }

    override fun hashCode(): Int {
        var result = depth
        result = 31 * result + content.hashCode()
        result = 31 * result + scope.hashCode()
        return result
    }

    override fun toString(): String {
        return "Token(content='$content', scope='$scope', depth=$depth, lineNumber=$lineNumber, startIndex=$startIndex, endIndex=$endIndex)"
    }
}
