package com.example.orchestrion.piano.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.orchestrion.piano.data.Touche
import com.example.orchestrion.piano.data.Touches

@Preview
@Composable
fun PianoScreen() {
    var pressedKeys by remember { mutableStateOf<Set<Touche>>(emptySet()) }

    Piano(
        touches = Touches,
        pressedKeys = pressedKeys,
        onKeyPressed = { touche -> pressedKeys = pressedKeys + touche },
        onKeyReleased = { touche -> pressedKeys = pressedKeys - touche }
    )
}