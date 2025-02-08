package com.example.orchestrion

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.OrientationEventListener
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.orchestrion.colorpicker.ColorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json.Default.configuration


@Preview(device = "spec:width=1600dp,height=891dp")
@Composable
fun PreviewPiano() {
    PianoUI(
        viewModel = ColorViewModel()
    )
}

@Composable
fun PianoUI(bleManager: BleManager? = null, viewModel: ColorViewModel) {

    val notes = mutableListOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C2", "C2#", "D2", "D2#", "E2", "F2", "F2#", "G2", "G2#", "A2", "A2#", "B2")


    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {

        Spacer(Modifier.height(20.dp))

        CustomSeekBarWithScroll(content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                // Dessiner les touches blanches
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter)
                ) {
                    for (whiteNote in notes){
                        if (!whiteNote.endsWith("#")) {
                            Log.d("KeyButton", "whiteNote: $whiteNote")
                            WhiteKey(
                                text = whiteNote,
                                color = viewModel.getTC(),
                                onPress =
                                { bleManager?.sendMidiMessage(1, string2Midi(whiteNote), 127, NoteON = true) },
                                onRelease =
                                { bleManager?.sendMidiMessage(1, string2Midi(whiteNote), 0, NoteON = false) }
                            )
                        }
                    }
                }


                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp) // Décaler les touches noires vers le haut
                ) {
                    Spacer(Modifier.width(48.dp))
                    //
                    for (blackNote in notes){
                        if (blackNote.endsWith("#")) { // Position des touches noires
                            Log.d("KeyButton", "blackNote: $blackNote")
                            BlackKey(
                                text = blackNote,
                                color = viewModel.getTC(),
                                onPress =
                                { bleManager?.sendMidiMessage(1, string2Midi(blackNote), 127, NoteON = true)
                                },
                                onRelease =
                                { bleManager?.sendMidiMessage(1, string2Midi(blackNote), 0, NoteON = false) }
                            )
                            if (blackNote.startsWith("D")||blackNote.startsWith("A") && !blackNote.startsWith("A2")) {
                                Spacer(Modifier.width(96.dp))
                            }else
                                Spacer(Modifier.width(22.dp))
                        }
                    }
                }
            }
        })
    }
}

fun string2Midi(note: String): Int? {
    val noteMap = mapOf(
        "C" to 0, "D" to 2, "E" to 4, "F" to 5, "G" to 7, "A" to 9, "B" to 11
    )

    val regex = Regex("([A-G])(\\d+)?(#?)")
    val match = regex.matchEntire(note)

    if (match != null) {
        var octave = 1
        val (notePart, octavePart, sharpPart) = match.destructured
        octave = if(octavePart.isEmpty())
            1
        else
            octavePart.toInt()

        return noteMap[notePart]?.let{it + octave * 12 + if (sharpPart.isNotEmpty()) 1 else 0}

    }else
        return 99999
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WhiteKey(
    text: String = "A1",
    color: Color = Color.White,
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {}
) {

    var backgroundColor by remember { mutableStateOf(Color.White) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {/*Pas besoin mais obligatoire*/},
        modifier = Modifier
            .width(74.dp)               // Largeur fixe à 74dp
            .fillMaxHeight()            // Hauteur égale à celle du parent (match_parent)
            .padding(start = 2.dp)        // Marge de début de 2dp
            // Gestion des événements tactiles pour ACTION_DOWN et ACTION_UP
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        backgroundColor = color
                        Log.d("KeyButton", "WhiteKey: $text")
                        coroutineScope.launch {
                            delay(100)
                        }
                        onPress()
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        backgroundColor = Color.White
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        backgroundColor = Color.White
                        onRelease()
                        true
                    }

                    else -> false
                }
            },
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RectangleShape, // Coins carrés (90°)
        // Padding en bas de 8dp pour aligner le contenu (texte)
        contentPadding = PaddingValues(bottom = 8.dp),
    ) {
        // Contenu du bouton : texte aligné en bas et centré horizontalement
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = text,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BlackKey(
    text: String = "C#",
    color: Color = Color.Black,
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {},
) {
    var backgroundColor by remember { mutableStateOf(Color.Black) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = { /* onClick vide, la gestion tactile se fait via pointerInteropFilter */ },
        modifier = Modifier
            .width(52.dp)
            .fillMaxHeight(0.8f)
            .offset(y = (-25).dp)      // Décalage vertical négatif de 25dp
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // À l'appui, on change la couleur de fond en #80ffe5 et on log l'index
                        backgroundColor = color
                        coroutineScope.launch {
                            delay(100)

                        }
                        onPress()
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        backgroundColor = Color.Black
                        onRelease()
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        backgroundColor = Color.Black
                        onRelease()
                        true
                    }

                    else -> false
                }
            },
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (text.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = text, color = Color.White)
            }
        }
    }
}

@Composable
fun CustomSeekBarWithScroll(content: @Composable () -> Unit = {}) {

    var sliderValue by remember { mutableFloatStateOf(50f) } // Initialiser à 50 pour centrer le thumb
    val scrollState = rememberScrollState()

    // Pour mesurer la largeur visible (du conteneur scrollable) et celle du contenu
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    val context = LocalContext.current
    val trackDrawable = ContextCompat.getDrawable(context, R.drawable.scrollbgm)
    val thumbDrawable = ContextCompat.getDrawable(context, R.drawable.thumb)
    val trackBitmap = (trackDrawable as BitmapDrawable).bitmap
    val thumbBitmap = (thumbDrawable as BitmapDrawable).bitmap

    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    val imageWidthPx = 1183f
    val density = LocalDensity.current.density
    val imageWidthDp = (imageWidthPx / density).dp

    // Calculer la largeur du slider en fonction de l'orientation
    val sliderWidth = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        (imageWidthPx / density).dp
    } else {
        configuration.screenWidthDp.dp // En paysage, utiliser la largeur de l'écran
    }

    // Après la composition, si les dimensions sont connues, on positionne le scroll à 50%
    LaunchedEffect(containerSize, contentSize) {
        if (contentSize.width > containerSize.width) {
            val maxScroll = contentSize.width - containerSize.width
            val targetScroll = (maxScroll * 0.5f).toInt() // Position à 50%
            scrollState.scrollTo(targetScroll)
        }
    }
    // Mettre à jour la valeur du slider quand le scroll change
    val currentScrollPosition = remember { derivedStateOf { scrollState.value } }
    LaunchedEffect(currentScrollPosition.value, containerSize, contentSize) {
        if (contentSize.width > containerSize.width) {
            val maxScroll = contentSize.width - containerSize.width
            val currentScroll = currentScrollPosition.value
            val newSliderValue = (currentScroll.toFloat() / maxScroll) * 100f
            sliderValue = newSliderValue
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    sliderValue = newValue
                    // Calcul de la position de scroll en fonction du slider (0 à 100)
                    if (contentSize.width > containerSize.width) {
                        val maxScroll = contentSize.width - containerSize.width
                        val targetScroll = (maxScroll * newValue / 100f).toInt()
                        coroutineScope.launch {
                            scrollState.scrollTo(targetScroll)
                        }
                    }
                },
                valueRange = 0f..100f,
                modifier = Modifier
                    .width(500.dp)
                    .drawBehind {
                        // Dessiner l'image du track
                        val trackHeight = trackBitmap.height.toFloat()
                        val trackY = size.height / 2 - trackHeight / 2
                        drawImage(
                            image = trackBitmap.asImageBitmap(),
                            topLeft = Offset(0f, trackY)
                        )
                    }
                    .drawWithContent {
                        // Dessiner l'image du thumb
                        drawContent()
                        // Calcul de la position du thumb sur la longueur du track
                        val trackWidth = size.width
                        val thumbWidth = thumbBitmap.width.toFloat()
                        val availableTrackWidth = trackWidth - thumbWidth
                        // Centrer le thumb
                        val thumbX = (sliderValue / 100f) * availableTrackWidth - thumbWidth / 2
                        val thumbY = size.height / 2 - thumbBitmap.height / 2
                        drawImage(
                            image = thumbBitmap.asImageBitmap(),
                            topLeft = Offset(thumbX, thumbY)
                        )
                    },
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                )
            )

            // "ScrollView" horizontal qui ne répond pas aux gestes tactiles (défilement manuel bloqué)
            Spacer(modifier = Modifier.height(50.dp))
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Mesurer la taille du conteneur visible
                    .onGloballyPositioned { coordinates ->
                        containerSize = coordinates.size
                    }
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        // Mesurer la taille totale du contenu
                        .onGloballyPositioned { coordinates ->
                            contentSize = coordinates.size
                        }
                ) {
                    content()
                }
            }
        }
    }
}

