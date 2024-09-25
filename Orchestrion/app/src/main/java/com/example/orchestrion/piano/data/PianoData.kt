package com.example.orchestrion.piano.data


data class Touche(val note: String, val isWhite: Boolean, val octave: Int)

val Touches = listOf(
    Touche("C", true, 1),   Touche("C#", false, 1), Touche("D", true, 1),   Touche("D#", false, 1), Touche("E", true, 1),
    Touche("F", true, 1),   Touche("F#", false, 1), Touche("G", true, 1),   Touche("G#", false, 1), Touche("A", true, 1),
    Touche("D#", false, 2), Touche("E", true, 2),   Touche("F", true, 2),   Touche("F#", false, 2), Touche("G", true, 2),
    Touche("A#", false, 1), Touche("B", true, 1),   Touche("C", true, 2),   Touche("C#", false, 2), Touche("D", true, 2),
    Touche("G#", false, 2), Touche("A", true, 2),   Touche("A#", false, 2), Touche("B", true, 2),   Touche("C", true, 3)
)