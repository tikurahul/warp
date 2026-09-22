package com.rahulrav.parser

import kotlin.test.Test

class TokensTest {
    @Test
    fun matchingTokensTest() {
        val code = """
        fun hello() {
          // {{}}
          // (
          // )
          // }
          // [
          // ]
          println("Hello")
        }
    """.trimIndent()

        val tokens = parseKotlin(code)
        buildRelatedTokens(tokens)
        tokens.forEach { token ->
            if (token.related != null) {
                println(token.related) // Matching Begin
                println(token)
            }
        }
    }

    @Test
    fun unmatchedTokensTest() {
        val code = """
        fun hello() {
          // Trying to corrupt the stack here.
          // Intentionally using no spaces for the next couple of lines.
        ]
        )
        )
          println("Hello")
        }
    """.trimIndent()

        val tokens = parseKotlin(code)
        buildRelatedTokens(tokens)
        tokens.forEach { token ->
            if (token.related != null) {
                println(token.related) // Matching Begin
                println(token)
            }
        }
    }
}
