package com.rahulrav.parser

import kotlin.test.Test

class ParserTest {
    @Test
    fun basicParsing() {
        val code = """
            val x = 10
        """.trimIndent()

        val tokens = parseKotlin(code)
        tokens.forEach { println(it) }
    }

    @Test
    fun functionParsing() {
        val code = """
            fun printLine(input: String) {
                println(input)
            }
        """.trimIndent()

        val tokens = parseKotlin(code)
        tokens.forEach { println(it) }
    }
}
