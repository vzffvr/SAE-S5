package com.example.orchestrion

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.orchestrion.colorpicker.ColorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Preview
@Composable
fun MainScreenPreview() {
    val viewModel: ColorViewModel = viewModel()


    MainScreen(
        navController = null,
        viewmodel = viewModel,
        bleManager = null
    )
}

@Composable
fun MainScreen(
    navController: NavController?,
    viewmodel: ColorViewModel,
    bleManager: BleManager?
) {
    var buttonColor = ButtonColors(
        Color.Transparent, Color.Black,
        Color.Transparent, Color.Black
    )
    var textcolor = Color.Black
    var buttonborder = BorderStroke(2.dp, Color.Black)
    var logo = R.drawable.symphonie_branding_light
    val options = listOf("Sinusoidale", "Carre", "Triangulaire")


    if (isSystemInDarkTheme()) {
        logo = R.drawable.symphonie_branding_dark
        textcolor = Color.White
        buttonColor = ButtonColors(
            Color.Transparent, Color.White,
            Color.Transparent, Color.White
        )
        buttonborder = BorderStroke(2.dp, Color.White)
    }


    var connectedColor by remember { mutableStateOf(Color.Red) }
    LaunchedEffect(bleManager?.isOrchestrionConnected()) {
        while (isActive) {
            connectedColor = if (bleManager?.isOrchestrionConnected() == true) {
                Color.Green
            } else {
                Color.Red
            }
            //Log.d("BLE", "Connected: ${bleManager?.isOrchestrionConnected()} - $ConnectedColor")
            delay(1000)
        }
    }

    //Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
        //.background(Color.Black)
//        .paint(
//        painterResource(id = R.drawable.background2),
//        contentScale = ContentScale.FillHeight)
    ) {
        val buttonModifier: Modifier =
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.28125f)
                .padding(16.dp)



        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(70.dp))

            //Logo
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .paint(
                            painterResource(logo),
                            contentScale = ContentScale.FillWidth
                        )

                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            //Boutton
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    shape = ShapeDefaults.ExtraLarge,
                    border = buttonborder,
                    colors = buttonColor,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(0.28125f)
                        .fillMaxWidth()
                        .fillMaxHeight(0.28125f)
                        .padding(16.dp),
                    onClick = {
                        navController?.navigate(Piano)
                    }
                ) {
                    Text(
                        text = "Clavier",
                        color = textcolor,
                        fontSize = 20.sp
                    )
                }

                /*
                                Button(
                                    shape = ShapeDefaults.ExtraLarge,
                                    border = buttonborder,
                                    colors = buttonColor,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.28125f)
                                        .padding(16.dp),
                                    onClick = {
                                        navController?.navigate(ConfigScreen)
                                    }) {
                                    Text(
                                        text = "Config",
                                        color = textcolor,
                                        fontSize = 20.sp
                                    )
                                }
                */

                Button(
                    shape = ShapeDefaults.ExtraLarge,
                    border = buttonborder,
                    colors = buttonColor,
                    modifier = Modifier
                        .weight(1f)

                        .fillMaxWidth()
                        .fillMaxHeight(0.28125f)
                        .padding(16.dp),
                    onClick = {
                        navController?.navigate(Colorpicker)
                    }) {
                    Text(
                        text = "ColorPicker",
                        color = viewmodel.getTC(),
                        fontSize = 20.sp
                    )
                }

                SpinnerConfig(
                    viewModel = viewmodel,
                    bleManager = bleManager,
                    options,
                    titre = "Forme du signal",
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Button(
                        shape = ShapeDefaults.ExtraLarge,
                        border = buttonborder,
                        colors = buttonColor,
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
