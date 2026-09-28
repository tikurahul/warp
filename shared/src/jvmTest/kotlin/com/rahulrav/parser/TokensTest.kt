package com.rahulrav.parser

import com.rahulrav.diff.frequencyAndContext
import com.rahulrav.diff.isBegin
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
        frequencyAndContext(tokens)
        tokens.forEach { token ->
            if (token.related != null && token.isBegin()) {
                println("${token.content} ${token.related!!.content}")
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
        frequencyAndContext(tokens)
        tokens.forEach { token ->
            if (token.related != null && token.isBegin()) {
                println("${token.content} ${token.related!!.content}")
            }
        }
    }
}
