package com.example.orchestrion

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

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
    var text by remember { mutableIntStateOf(viewmodel.channel) }
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
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                //Logo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    //Logo
                    Box(
                        modifier = Modifier
                            .paint(
                                painterResource(logo),
                                contentScale = ContentScale.FillWidth
                            )

                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SpinnerConfig(bleManager, options, "Forme du signal", Modifier)
                    var oldChannel = viewmodel.channel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(0.8f),
                            value = text.toString(),
                            onValueChange = { it ->

                                text = it.toInt()
                            },
                            label = { Text(text = "Channel Midi") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (text !in 0..15) {
                                        viewmodel.channel =
                                            text.toString().trim().replace("\n", "").toIntOrNull()!!
                                    } else {
                                        text = oldChannel
                                    }
                                }
                            ),

                            )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Button(
                            shape = ShapeDefaults.ExtraLarge,
                            border = buttonborder,
                            colors = ButtonColors(
                                Color.Transparent, Color.White,
                                Color.Transparent, Color.White
                            ),
                            modifier = Modifier
                                .padding(6.dp),
                            onClick = {
                                bleManager?.reconnectToESP32()
                            }) {
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

