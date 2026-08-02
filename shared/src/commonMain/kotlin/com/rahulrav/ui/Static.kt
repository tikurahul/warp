package com.rahulrav.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.rahulrav.parser.Token

// Simply render the parsed content

@Composable
fun Static(
    modifier: Modifier,
    tokens: List<Token>,
) {
    val rows = tokens.groupBy { it.lineNumber }.toSortedMap()
    Column(modifier = modifier.fillMaxSize()) {
        rows.forEach { (_, tokens) ->
            Row {
                val sorted = tokens.sortedBy { it.startIndex }
                sorted.forEach { token ->
                    Text(
                        text = token.content,
                        color = token.color(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = FONT_SIZE.sp,
                        lineHeight = LINE_HEIGHT.sp,
                    )
                }
            }
        }
    }
}
