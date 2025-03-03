package com.example.orchestrion.Screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.orchestrion.BleManager
import com.example.orchestrion.Melodie
import com.example.orchestrion.R
import com.example.orchestrion.SpinnerConfig
import com.example.orchestrion.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import sendAllNotesOff

@Preview
@Composable
fun ConfigScreenPreview() {
    val viewModel: ViewModel = viewModel()

    ConfigScreen(
        navController = null,
        viewmodel = viewModel,
        bleManager = null
    )
}

@Composable
fun ConfigScreen(
    navController: NavController?,
    viewmodel: ViewModel,
    bleManager: BleManager?
) {
    val context = LocalContext.current
    val options = listOf("Sinusoidale", "Carre", "Triangulaire")
    var buttonColor = ButtonColors(
        Color.Transparent, Color.Black,
        Color.Transparent, Color.Black
    )

    var buttonborder = BorderStroke(2.dp, Color.Black)
    var logo = R.drawable.symphonie_branding_light
    var connectedColor by remember { mutableStateOf(Color.Red) }

    if (isSystemInDarkTheme()) {
        logo = R.drawable.symphonie_branding_dark
        buttonColor = ButtonColors(
            Color.Transparent, Color.White,
            Color.Transparent, Color.White
        )
        buttonborder = BorderStroke(2.dp, Color.White)
    }

    LaunchedEffect(bleManager?.isSymphonieConnected()) {
        while (isActive) {
            connectedColor = if (bleManager?.isSymphonieConnected() == true) {
                Color.Green
            } else {
                Color.Red
            }
            //Log.d("BLE", "Connected: ${bleManager?.isOrchestrionConnected()} - $ConnectedColor")
            delay(1000)
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Partie supérieure : le logo reste en haut
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .paint(
                            painterResource(logo),
                            contentScale = ContentScale.FillWidth
                        )
                        .fillMaxWidth()
                )
            }
            // Espace optionnel entre le logo et la partie centrale
            Spacer(modifier = Modifier.height(16.dp))

            // Partie centrale : les widgets sont centrés dans l'espace restant
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var text by remember { mutableStateOf(bleManager?.channel.toString()) }
                        var oldChannel by remember { mutableStateOf(bleManager?.channel.toString()) }

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            value = text,
                            onValueChange = { newText ->
                                // Filtrer pour ne garder que les chiffres
                                if (newText.all { it.isDigit() }) {
                                    text = newText
                                }
                            },
                            label = { Text(text = "Channel Midi") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val number = text.toIntOrNull()
                                    if (number != null && number in 1..16) {
                                        if (bleManager != null) {
                                            bleManager.channel = number
                                        }
                                        oldChannel = text // Sauvegarde la valeur correcte
                                    } else {
                                        text = oldChannel // Restaure l'ancienne valeur valide
                                        Toast.makeText(context, "Valeur incorrecte", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SpinnerConfig(bleManager, options, "Forme du signal", Modifier)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        shape = ShapeDefaults.ExtraLarge,
                        border = buttonborder,
                        colors = buttonColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        onClick = {
                            bleManager?.let { sendAllNotesOff(it) }
                        }
                    ) {
                        Text(
                            text = "Reset des touches appuyées",
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            shape = ShapeDefaults.ExtraLarge,
                            border = buttonborder,
                            colors = ButtonColors(
                                Color.Transparent, Color.White,
                                Color.Transparent, Color.White
                            ),
                            modifier = Modifier.padding(6.dp),
                            onClick = {
                                bleManager?.reconnectToESP32()
                            }
                        ) {
                            Text(
                                text = "Reconnect",
                                color = connectedColor,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

