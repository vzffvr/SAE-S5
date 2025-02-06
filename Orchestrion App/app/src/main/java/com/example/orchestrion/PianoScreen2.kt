package com.example.orchestrion

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//val whiteButtonStyle = Modifier
//    .weight(1f)
//    .width(74.dp)
//    .fillMaxHeight()
//    .padding(2.dp,0.dp,0.dp,8.dp,)

val blackButtonStyle = Modifier
    .width(52.dp)
    .fillMaxHeight()
    .padding(2.dp, 0.dp, 0.dp, 8.dp)// Gravity center


@Composable
//@Preview
fun Previewbutton() {
    WhiteKeyButton("A1", 1)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WhiteKeyButton(
    text: String,
    index: Int,
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {}
) {
    // Couleur de fond initiale : blanc
    var backgroundColor by remember { mutableStateOf(Color.White) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = { /* Gestion tactile assurée par pointerInteropFilter */ },
        modifier = Modifier
            .width(74.dp)               // Largeur fixe à 74dp
            .fillMaxHeight()            // Hauteur égale à celle du parent (match_parent)
            .padding(start = 2.dp)        // Marge de début de 2dp
            // Gestion des événements tactiles pour ACTION_DOWN et ACTION_UP
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Change le fond en #80ffe5 lors de l'appui et log l'index
                        backgroundColor = Color(android.graphics.Color.parseColor("#80ffe5"))
                        Log.d("KeyButton", "index: $index")
                        coroutineScope.launch {
                            delay(100)
                            backgroundColor = Color.White
                        }
                        onPress()
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        onRelease()
                        true
                    }

                    else -> false
                }
            },
        // Application de la couleur de fond dynamique et forçage des coins à 90°
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RectangleShape, // Coins carrés (90°)
        // Padding en bas de 8dp pour aligner le contenu (texte)
        contentPadding = PaddingValues(bottom = 8.dp)
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

//@Preview
@Composable
fun SharpkeyPreview() {
    SharpKeyButton(0, "B1")
}

@Preview
@Composable
fun CustomSeekBarWithScroll() {
    // État du slider (valeur entre 0 et 100, initialement 50)
    var sliderValue by remember { mutableStateOf(50f) }
    val scrollState = rememberScrollState()

    // Pour mesurer la largeur visible (du conteneur scrollable) et celle du contenu
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    val coroutineScope = rememberCoroutineScope()

    // Après la composition, si les dimensions sont connues, on positionne le scroll à 55%
    LaunchedEffect(containerSize, contentSize) {
        if (contentSize.width > containerSize.width) {
            val maxScroll = contentSize.width - containerSize.width
            val targetScroll = (maxScroll * 0.55f).toInt()
            scrollState.scrollTo(targetScroll)
            // Mettre à jour le slider en conséquence
            sliderValue = targetScroll * 100f / maxScroll
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Slider imitant le SeekBar
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
                .padding(start = 100.dp, end = 100.dp, top = 10.dp, bottom = 10.dp)
            // Pour reproduire exactement thumb/background, il faudrait customiser le Slider.
        )

        // "ScrollView" horizontal qui ne répond pas aux gestes tactiles (défilement manuel bloqué)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                // Mesurer la taille du conteneur visible
                .onGloballyPositioned { coordinates ->
                    containerSize = coordinates.size
                }
                // Consommer tous les événements tactiles pour bloquer le scroll manuel
                .noTouchScrolling()
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    // Mesurer la taille totale du contenu
                    .onGloballyPositioned { coordinates ->
                        contentSize = coordinates.size
                    }
            ) {
                // Exemple de contenu : ici, vous pouvez placer vos éléments.
                // Pour la démonstration, créons plusieurs Box.
                for (i in 1..10) {
                    Box(
                        modifier = Modifier
                            .size(width = 150.dp, height = 200.dp)
                            .padding(4.dp)
                            .background(Color.LightGray)
                    )
                }
            }
        }
    }
}

/**
 * Modifier qui consomme tous les événements tactiles pour empêcher
 * que le contenu ne soit scrollé manuellement par l'utilisateur.
 */
fun Modifier.noTouchScrolling() = composed {
    pointerInput(Unit) {
        // Consomme tous les événements tactiles
        awaitPointerEventScope {
            // Attend et consomme tous les événements tactiles sans rien faire
            while (true) {
                awaitPointerEvent()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SharpKeyButton(
    index: Int,
    text: String = "",
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {},
    // Le texte est optionnel (souvent vide pour les touches noires)
) {
    // Couleur de fond initiale : noir (pour simuler @drawable/black_key_background)
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
                        backgroundColor = Color(android.graphics.Color.parseColor("#80ffe5"))
                        println("SharpKeyButton index: $index pressed")
                        coroutineScope.launch {
                            delay(100)
                            backgroundColor = Color.Black
                        }
                        onPress()
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        onRelease()
                        true
                    }

                    else -> false
                }
            },
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RectangleShape, // Coins à 90° (aucun arrondi)
        contentPadding = PaddingValues(0.dp)
    ) {
        if (text.isNotEmpty()) {
            // Si un texte est défini, on l'affiche centré, en blanc pour contraster avec le fond noir
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
fun PianoUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {


        // Top Section with SeekBar
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF272727))
        ) {
            Slider(
                value = 50f,
                onValueChange = { /* Handle value change */ },
                valueRange = 0f..100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 100.dp, vertical = 10.dp)
            )
        }

        // Piano Keys Section
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .background(Color.Black)
            ) {
                // White Keys
                listOf(
                    "A1",
                    "B1",
                    "C1",
                    "D1",
                    "E1",
                    "F1",
                    "G1",
                    "A2",
                    "B2",
                    "C2",
                    "D2",
                    "E2",
                    "F2",
                    "G2"
                ).forEach { key ->
                    Button(
                        onClick = { /* Handle key press */ },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),

                        ) {
                        Text(text = key)
                    }
                }
            }

            // Black Keys
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Spacer(modifier = Modifier.width(52.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(100.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(22.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(101.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(25.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(26.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(100.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(22.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(101.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}

                Spacer(modifier = Modifier.width(25.dp))
                Button(
                    onClick = { /* Handle key press */ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {}
            }
        }
    }
}