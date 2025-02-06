import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Composable générique pour une touche du piano.
 *
 * Les boutons utilisent des styles définis dans vos ressources (par ex. KeyButtonStyle ou SharpkeyButtonStyle)
 * et déclenchent un callback à l'appui (onPress) et au relâchement (onRelease).
 */
@Composable
fun KeyButton(
    text: String = "",
    onPress: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
    // Les styles appliqués à ce Button (via le thème) reproduisent ceux définis dans le XML.
) {
    Button(
        onClick = { /* onClick vide car nous gérons onPress/onRelease */ },
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    onPress() // Appel dès l'appui
                    if (tryAwaitRelease()) { // Attend le relâchement
                        onRelease() // Appel au relâchement
                    }
                }
            )
        }
    ) {
        if (text.isNotEmpty()) {
            Text(text = text)
        }
    }
}

/**
 * Ecran du piano en Compose.
 *
 * La structure reprend celle de votre XML :
 * - Une zone supérieure (weight = 1) avec un Slider et un fond #272727.
 * - Une zone inférieure (weight = 2) défilante horizontalement qui superpose deux rangées :
 *   - La rangée des touches blanches (avec un background défini dans le style KeyButtonStyle).
 *   - La rangée des touches noires (height = 180dp) avec des marges spécifiques et le style SharpkeyButtonStyle.
 *
 * Pour forcer l'orientation paysage, pensez à le configurer dans le manifeste ou l'activité.
 */

@Preview
@Composable
fun PianoScreen() {
    // État du slider (équivalent à la SeekBar avec progress 50 et max 100)
    var sliderValue by remember { mutableStateOf(50f) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Zone supérieure : Slider sur fond #272727
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF272727))
        ) {
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0f..100f,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 100.dp, end = 100.dp, top = 10.dp, bottom = 10.dp)
            )
        }

        // Zone inférieure : Piano dans un conteneur horizontal scrollable
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            // Utilisation d'une Box pour superposer les rangées de touches
            Box(modifier = Modifier.fillMaxSize()) {
                // Rangée des touches blanches (background défini dans le style du bouton, ici simulé par le thème)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black) // La couleur de fond provient du XML (@color/black)
                ) {
                    val whiteKeys = listOf(
                        "A1", "B1", "C1", "D1", "E1", "F1", "G1",
                        "A2", "B2", "C2", "D2", "E2", "F2", "G2"
                    )

                    whiteKeys.forEach { key ->
                        KeyButton(
                            text = key,
                            onPress = { println("$key pressed") },
                            onRelease = { println("$key released") },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }

                // Rangée des touches noires (sharp)
                // Placée en superposition sur les touches blanches (hauteur fixe 180dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .align(Alignment.TopStart)
                ) {
                    // La liste contient le nom de la touche et la marge de début (layout_marginStart) d'après le XML.
                    val blackKeys = listOf(
                        "A1Sharp" to 52.dp,
                        "C1Sharp" to 100.dp,
                        "D1Sharp" to 22.dp,
                        "F1Sharp" to 101.dp,
                        "G1Sharp" to 25.dp,
                        "A2Sharp" to 26.dp,
                        "C2Sharp" to 100.dp,
                        "D2Sharp" to 22.dp,
                        "F2Sharp" to 101.dp,
                        "G2Sharp" to 25.dp
                    )

                    blackKeys.forEach { (key, marginStart) ->
                        Spacer(modifier = Modifier.width(marginStart))
                        KeyButton(
                            text = "", // Texte vide pour les touches noires
                            onPress = { println("$key pressed") },
                            onRelease = { println("$key released") },
                            modifier = Modifier
                                .width(40.dp)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}