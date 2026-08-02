package com.rahulrav.diff

import com.rahulrav.parser.parseKotlin
import org.intellij.lang.annotations.Language
import kotlin.test.Test

class HeckelDiffTest {
    @Test
    fun basicDiff() {
        @Language("kotlin")
        val slideP = """
            val x = 10
        """.trimIndent()

        @Language("kotlin")
        val slideC = """
            val y = 10
        """.trimIndent()

        val previous = parseKotlin(code = slideP)
        val current = parseKotlin(code = slideC)
        val changes = diff(previous = previous, current = current)
        changes.forEach { change ->
            println(change)
        }
    }

    @Test
    fun basicDiff2() {
        @Language("kotlin")
        val slideP = """
            val x = 10
        """.trimIndent()

        @Language("kotlin")
        val slideC = """
            val x = 20
        """.trimIndent()

        val previous = parseKotlin(code = slideP)
        val current = parseKotlin(code = slideC)
        val changes = diff(previous = previous, current = current)
        changes.forEach { change ->
            println(change)
        }
    }

    @Test
    fun lineMoveDiff() {
        @Language("kotlin")
        val slideP = """
            val x = 10
            fun convert(input: Int) {
              // ...
            }
        """.trimIndent()

        @Language("kotlin")
        val slideC = """
            val x = 10
            fun convert(input: Int) {
              println("The actual implementation")
            }
        """.trimIndent()

        val previous = parseKotlin(code = slideP)
        val current = parseKotlin(code = slideC)
        val changes = diff(previous = previous, current = current)
        changes.forEach { change ->
            println(change)
        }
    }

    @Test
    fun lineMoveDiff2() {
        @Language("kotlin")
        val slideP = """
            val a = 10
            val b = 20
            val c = 30
        """.trimIndent()

        @Language("kotlin")
        val slideC = """
            val b = 20
            val c = 30
            val d = 40
        """.trimIndent()

        val previous = parseKotlin(code = slideP)
        val current = parseKotlin(code = slideC)
        val changes = diff(previous = previous, current = current)
        changes.forEach { change ->
            println(change)
        }
    }
}
