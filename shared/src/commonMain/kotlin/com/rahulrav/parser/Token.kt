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
    val scope: String,/* More context for animations. */
    /** The depth of the primary scope. */
    val depth: Int, val lineNumber: Int, val startIndex: Int, val endIndex: Int
) {
    /** The underlying content id that was assigned to the token.
     * This is guaranteed to be stable across a deck. */
    private var contentId: String? = null

    /**
     * A related token, when applicable. This is only ever populated for [Token]s that are
     * brackets. So `}`, `]`, `>`, `)` will point to their corresponding matching begin pairs.
     *
     * This helps ensure that once we find a match for one of these [Token]s, we also match the
     * corresponding matching begin tokens.
     */
    public var related: Token? = null
        private set

    /**
     * What `index` does the [Token] occur in, after a parse tree was constructed.
     */
    public var index: Int = 0
        private set

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

    fun assignRelated(match: Token) {
        this.related = match
    }

    fun assignIndex(index: Int) {
        this.index = index
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Token

        if (content != other.content) return false
        if (scope != other.scope) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 31 * content.hashCode()
        result = 31 * result + scope.hashCode()
        return result
    }

    override fun toString(): String {
        return "Token(content='$content', scope='$scope', depth=$depth, lineNumber=$lineNumber, startIndex=$startIndex, endIndex=$endIndex)"
    }
}
