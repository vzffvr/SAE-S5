import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.orchestrion.BleManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class Melody(
    val name: String,
    val notes: List<Note>
)

data class Note(
    val midiNote: Int,    // 0-127
    val durationMs: Long  // Durée en millisecondes
)

@Composable
fun MelodiePlayer(bleManager: BleManager) {

    val scope = rememberCoroutineScope()
    var playbackJob by remember { mutableStateOf<Job?>(null) }
    var currentMelody by remember { mutableStateOf<Melody?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val melodies = listOf(
        Melody("Baby Shark", listOf(
            Note(24, 300), Note(24, 300), Note(24, 300), // DO1
            Note(36, 600), // DO2
            Note(24, 300), Note(24, 300), Note(24, 300),
            Note(36, 600)
        )),
        Melody("Super Mario", listOf(
            Note(24, 150), Note(24, 150), Note(24, 150), // DO1
            Note(19, 150), // FA0
            Note(21, 150), // SOL0
            Note(24, 300), Note(28, 300), // DO1, MI1
            Note(19, 150), Note(19, 150), Note(19, 150)
        )),
        Melody("Star Wars", listOf(
            Note(24, 500), Note(24, 500), Note(24, 500), // DO1
            Note(36, 400), // DO2
            Note(33, 150), // LA1
            Note(31, 150), // SOL#1
            Note(29, 150), // FA#1
            Note(28, 400)  // MI1
        )),
        Melody("Pirates", listOf(
            Note(24, 200), Note(28, 200), Note(31, 200), // DO1, MI1, SOL#1
            Note(24, 200), Note(28, 200), Note(31, 200),
            Note(24, 400), Note(31, 400)
        )),
        Melody("Pirates des Caraïbes", listOf(
            Note(24, 200), Note(28, 200), Note(31, 200),
            Note(24, 200), Note(28, 200), Note(31, 200),
            Note(24, 400), Note(31, 400),

            Note(26, 200), Note(29, 200), Note(33, 200),
            Note(26, 200), Note(29, 200), Note(33, 200),
            Note(26, 400), Note(33, 400),

            Note(28, 200), Note(31, 200), Note(36, 200),
            Note(28, 200), Note(31, 200), Note(36, 200),
            Note(28, 400), Note(36, 400),

            Note(26, 200), Note(29, 200), Note(33, 200),
            Note(26, 200), Note(29, 200), Note(33, 200),
            Note(26, 400), Note(33, 400)
        )),

    )

    BackHandler {
        playbackJob?.cancel()
        sendAllNotesOff(bleManager)
        isPlaying = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sélection de mélodie
        melodies.forEach { melody ->
            Button(
                onClick = { currentMelody = melody },
                enabled = !isPlaying
            ) {
                Text(melody.name)
            }
        }

        // Contrôles
        currentMelody?.let { melody ->
            Text("Mélodie sélectionnée : ${melody.name}")

            Button(
                onClick = {
                    if (!isPlaying) {
                        isPlaying = true
                        playbackJob = scope.launch {
                            playMelody(melody, bleManager)
                            isPlaying = false
                        }
                    }
                },
                enabled = !isPlaying
            ) {
                Text("Jouer")
            }

            Button(
                onClick = {
                    playbackJob?.cancel()
                    sendAllNotesOff(bleManager)
                    isPlaying = false
                }
            ) {
                Text("Arrêter")
            }
        }
    }
}

private suspend fun playMelody(melody: Melody, bleManager: BleManager) {
    melody.notes.forEach { note ->
        bleManager.sendMidiMessage(
            channel = 1,
            note = note.midiNote,
            velocity = 127, // Velocity max enveloppée dans le byte
            NoteON = true
        )

        delay(note.durationMs)

        bleManager.sendMidiMessage(
            channel = 1,
            note = note.midiNote,
            velocity = 0, // Note OFF via velocity=0
            NoteON = false
        )
    }
}

private fun sendAllNotesOff(bleManager: BleManager) {
    // Envoie un Note OFF pour toutes les notes possibles
    repeat(128) { note ->
        bleManager.sendMidiMessage(1, note, 0, false)
    }
}
