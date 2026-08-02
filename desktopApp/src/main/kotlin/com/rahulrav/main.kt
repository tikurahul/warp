package com.rahulrav

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val windowState = rememberWindowState(
        width = 3600.dp,
        height = 2400.dp,
        position = WindowPosition.Aligned(alignment = Alignment.Center)
    )
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Warp ✨",
    ) {
        Warp()
    }
}
