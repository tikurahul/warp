package com.rahulrav.ui

import org.intellij.lang.annotations.Language

@Language("kotlin")
val BASIC_SLIDES = listOf(
    """
        val x = 10
    """.trimIndent(),
    """
        val x = 20
    """.trimIndent()
)

@Language("kotlin")
val LINE_MOVES = listOf(
    """
        fun sequence() {
          val x = one()
          val y = two()
          done()
        }
    """.trimIndent(),
    """
        fun sequence() {
          val y = two()
          val x = one()
          done()
        }
    """.trimIndent()
)

@Language("kotlin")
val LINE_MOVES_2 = listOf(
    """
        fun sequence() {
          val x = one()
          val y = two()
          // ...
          val z = three()
          done()
        }
    """.trimIndent(),
    """
        fun sequence() {
          val y = two()
          val x = one()
          // ...
          val z = three()
          val m = four()
          done()
        }
    """.trimIndent()
)

@Language("kotlin")
val BLOCK_MOVES = listOf(
    """
    fun inner() {
      // An inner block
      val x = 10
      println(x)
    }
    """.trimIndent(),
    """
    fun outer() {
      fun inner() {
        // An inner block
        val x = 10
        println(x)
      }
    }
    """.trimIndent(),
    """
    fun outer2() {
      fun outer() {
        fun inner() {
          // An inner block
          val x = 10
          println(x)
        }
      }
    }
    """.trimIndent(),
)

@Language("kotlin")
val TRACING_SLIDES = listOf(
    """
      import androidx.tracing.Tracer
      import androidx.tracing.DelicateTracingApi
      import androidx.tracing.wire.TraceDriver
      import androidx.tracing.wire.TraceSink
      import java.io.File

      fun main() {
          
      }
    """.trimIndent(),

    """
      import androidx.tracing.Tracer
      import androidx.tracing.DelicateTracingApi
      import androidx.tracing.wire.TraceDriver
      import androidx.tracing.wire.TraceSink
      import java.io.File

      fun main() {
          // Create the TraceSink, and the `TraceDriver`

          // Register the tracer

          // Call driver.close() as a result of the process shutdown hook.
      }
    """.trimIndent(),


    """
      import androidx.tracing.Tracer
      import androidx.tracing.DelicateTracingApi
      import androidx.tracing.wire.TraceDriver
      import androidx.tracing.wire.TraceSink
      import java.io.File

      fun main() {
          // Create the TraceSink, and the `TraceDriver`
          val outputDirectory = File("/tmp/perfetto")
          val sink = TraceSink(directory = outputDirectory)
          val driver = TraceDriver(sink = sink, isEnabled = true)

          // Register the tracer

          // Call driver.close() as a result of the process shutdown hook.
      }
    """.trimIndent(),

    """
      import androidx.tracing.Tracer
      import androidx.tracing.DelicateTracingApi
      import androidx.tracing.wire.TraceDriver
      import androidx.tracing.wire.TraceSink
      import java.io.File

      fun main() {
          // Create the TraceSink, and the `TraceDriver`
          val outputDirectory = File("/tmp/perfetto")
          val sink = TraceSink(directory = outputDirectory)
          val driver = TraceDriver(sink = sink, isEnabled = true)

          // Register the tracer
          @OptIn(DelicateTracingApi::class)
          Tracer.setGlobalTracer(driver.tracer)

          // Call driver.close() as a result of the process shutdown hook.
          // ...
      }
    """.trimIndent(),

    """
      import androidx.tracing.Tracer
      import androidx.tracing.DelicateTracingApi
      import androidx.tracing.wire.TraceDriver
      import androidx.tracing.wire.TraceSink
      import java.io.File

      fun main() {
          // Create the TraceSink, and the `TraceDriver`
          val outputDirectory = File("/tmp/perfetto")
          val sink = TraceSink(directory = outputDirectory)
          val driver = TraceDriver(sink = sink, isEnabled = true)

          // Register the tracer
          @OptIn(DelicateTracingApi::class)
          Tracer.setGlobalTracer(driver.tracer)

          // Call driver.close() as a result of the process shutdown hook.
          Runtime.getRuntime().addShutdownHook(Thread {
              driver.close()
          })
      }
    """.trimIndent()
)

val SLIDE_PAIRS = TRACING_SLIDES.zipWithNext()
