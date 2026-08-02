package com.rahulrav.parser

import org.eclipse.tm4e.core.grammar.IGrammar
import org.eclipse.tm4e.core.grammar.IStateStack
import org.eclipse.tm4e.core.grammar.IToken
import org.eclipse.tm4e.core.grammar.ITokenizeLineResult
import org.eclipse.tm4e.core.internal.grammar.StateStack
import org.eclipse.tm4e.core.registry.IGrammarSource
import org.eclipse.tm4e.core.registry.IRegistryOptions
import org.eclipse.tm4e.core.registry.Registry
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

// The name of the scope
internal const val SCOPE_KOTLIN = "source.kotlin"

internal val REGISTRY: Registry = Registry(object : IRegistryOptions {
    override fun getGrammarSource(scopeName: String): IGrammarSource {
        return when (scopeName) {
            SCOPE_KOTLIN -> kotlinGrammar()
            else -> throw IllegalStateException("Unsupported scope: $scopeName")
        }
    }

    private fun kotlinGrammar(): IGrammarSource {
        // https://raw.githubusercontent.com/nishtahir/language-kotlin/refs/heads/master/dist/Kotlin.tmLanguage
        val stream = Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("grammar/kotlin.tmLanguage")
        val grammarText = stream.use { stream!!.bufferedReader().readText() }
        // XML content type, given it's a plist
        val grammar = IGrammarSource.fromString(IGrammarSource.ContentType.XML, grammarText)
        require(grammar != null) { "Unable to load grammar for $SCOPE_KOTLIN" }
        return grammar
    }
})

internal val GRAMMAR: IGrammar? = REGISTRY.loadGrammar(SCOPE_KOTLIN)
internal val DURATION = 1.minutes.toJavaDuration()

/**
 * Uses `TextMate for Eclipse`, and its API to build parse trees for Kotlin.
 * In general, we can extend this to any language that has TextMate grammar.
 */
actual fun parseKotlin(code: String): List<Token> {
    require(GRAMMAR != null) { "Unable to load grammar for $SCOPE_KOTLIN" }
    // Keeps track of parse state
    var state: IStateStack = StateStack.NULL
    val tokens = mutableListOf<Token>()
    code.lines().forEachIndexed { lineNumber, line ->
        val result: ITokenizeLineResult<Array<IToken>> = GRAMMAR.tokenizeLine(
            /* lineText = */ line,
            /* prevState = */ state,
            /* timeLimit = */ DURATION
        )
        // This should never really happen
        require(!result.isStoppedEarly)
        result.tokens.forEach { token ->
            // TM4E sometimes implicitly adds line-endings to lines when using RegExp matchers.
            // For e.g. Comments are matched with a `<pattern>$` and therefore an implicit
            // line ending is added. We therefore need to clamp start and end indexes.
            val startIndex = token.startIndex.coerceIn(0, line.length)
            val endIndex = token.endIndex.coerceIn(startIndex, line.length)
            val content = line.substring(startIndex = startIndex, endIndex = endIndex)
            val scopes = token.scopes
            // The primary scope is always the last one.
            val scope = scopes.last()
            val depth = scopes.size
            tokens += Token(
                content = content,
                scope = scope,
                depth = depth,
                lineNumber = lineNumber,
                startIndex = startIndex,
                endIndex = endIndex
            )
        }
        // Update state
        state = result.ruleStack
    }
    return tokens
}
