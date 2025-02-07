package com.example.orchestrion

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.MotionEvent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.orchestrion.colorpicker.ColorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//@Preview(device = "spec:width=600dp,height=891dp")
//@Composable
//fun PreviewPiano() {
//    PianoUI(
//        viewModel = ColorViewModel()
//    )
//}

@Composable
fun PianoUI(bleManager: BleManager? = null, viewModel: ColorViewModel) {
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
                    repeat(14) { // 14 touches blanches pour une octave
                        WhiteKey(
                            color = viewModel.getTC(),
                            onPress = {
                                bleManager?.sendMidiMessage(1, it, 127)
                            },
                            onRelease = {
                                bleManager?.sendMidiMessage(1, it, 0)
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp) // Décaler les touches noires vers le haut
                ) {
                    repeat(14) { index ->
                        if (index % 7 != 2 && index % 7 != 6) { // Position des touches noires
                            BlackKey(
                                color = viewModel.getTC(),
                                onPress = {
                                    bleManager?.sendMidiMessage(1, index, 127)
                                },
                                onRelease = {
                                    bleManager?.sendMidiMessage(1, index, 0)
                                }
                            )

                        } else {
                            Spacer(modifier = Modifier.width(20.dp)) // Espace pour les touches blanches
                        }
                    }
                }
            }
        })
    }
}

@Composable
fun PianoKeyboard(octaves: Int) {
    val whiteKeysPattern = listOf(true, false, true, true, false, true, false, true, true, false, true, false)
    val blackKeysPattern = listOf(false, true, false, false, true, false, true, false, false, true, false, true)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            // Dessiner les touches blanches
            Row {
                repeat(octaves) { octave ->
                    for (note in 0 until 12) {
                        if (whiteKeysPattern[note]) {
                            WhiteKey()
                        }
                    }
                }
            }

            // Dessiner les touches noires
            Row(modifier = Modifier.padding(top = 50.dp)) {
                repeat(octaves) { octave ->
                    for (note in 0 until 12) {
                        if (blackKeysPattern[note]) {
                            BlackKey()
                        } else {
                            // Espacement pour aligner les touches noires
                            Spacer(modifier = Modifier.width(30.dp))
                        }
                    }
                }
            }
        }
    }
}


@Preview(device = "spec:width=1200dp,height=891dp")
@Composable
fun PreviewPiano() {
    PianoKeyboard(octaves = 1) // Aperçu avec 2 octaves
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WhiteKey(
    text: String = "A1",
    index: Int = 0,
    color: Color = Color.White,
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {}
) {

    var backgroundColor by remember { mutableStateOf(Color.White) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {/*Pas besoin mais obligatoire*/ },
        modifier = Modifier
            .width(74.dp)               // Largeur fixe à 74dp
            .fillMaxHeight()            // Hauteur égale à celle du parent (match_parent)
            .padding(start = 2.dp)        // Marge de début de 2dp
            // Gestion des événements tactiles pour ACTION_DOWN et ACTION_UP
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        backgroundColor = color
                        Log.d("KeyButton", "index: $index")
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
    index: Int = 0,
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
                        println("SharpKeyButton index: $index pressed")
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
                    .fillMaxWidth()
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

