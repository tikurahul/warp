package com.rahulrav

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val FONT_SIZE = 48
private const val LINE_HEIGHT = 1.5 * FONT_SIZE

@Composable
fun Warp() {
    val hScrollState = rememberScrollState()
    val vScrollState = rememberScrollState()
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(128.dp)
                    .verticalScroll(vScrollState)
                    .horizontalScroll(hScrollState)
            ) {
                // language="kotlin"
                Text(
                    text = """
                        val x = 10
                        println("Hello")
                    """.trimIndent(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = FONT_SIZE.sp,
                    lineHeight = LINE_HEIGHT.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
