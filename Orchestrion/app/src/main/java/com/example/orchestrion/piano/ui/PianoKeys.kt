package com.example.orchestrion.piano.ui

import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.example.orchestrion.piano.data.Touche

@Composable
fun PianoKey(touche: Touche, isPressed: Boolean, onKeyPressed: (Touche) -> Unit, onKeyReleased: (Touche) -> Unit) {
    val color = if (touche.isWhite) Color.White else Color.Black
    val modifier = Modifier
        .fillMaxHeight()
        .background(color)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    onKeyPressed(touche)
                    tryAwaitRelease()
                    onKeyReleased(touche)
                }
            )
        }

    if (isPressed) {
        Modifier.background(if (touche.isWhite) Color.LightGray else Color.DarkGray)
    }

    Box(modifier)
}

@Composable
fun Piano(touches: List<Touche>, pressedKeys: Set<Touche>, onKeyPressed: (Touche) -> Unit, onKeyReleased: (Touche) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        touches.chunked(12).forEach { octaveKeys ->
            Row(modifier = Modifier.fillMaxWidth()) {
                octaveKeys.forEach { touche ->
                    PianoKey(touche, pressedKeys.contains(touche), onKeyPressed, onKeyReleased)
                }
            }
        }
    }
}