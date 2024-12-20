package com.example.orchestrion

import android.content.Intent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.orchestrion.colorpicker.ColorViewModel
import java.util.Timer
import java.util.TimerTask

@Preview
@Composable
fun MainScreenPreview() {
    val viewModel: ColorViewModel = viewModel()


    MainScreen(
        navController = null,
        viewmodel = viewModel,
        myMQTT = MqttClientManager("tcp://10.42.0.1:1883", LocalContext.current)
    )
}

@Composable
fun MainScreen(
    navController: NavController?,
    viewmodel: ColorViewModel,
    myMQTT: MqttClientManager
) {

    var connectedcolor = Color.Red

    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            println("Timer ticked!")
            if (myMQTT.isConnected()) {
                connectedcolor = Color.Green
            } else {
                connectedcolor = Color.Red
            }

        }
    }, 0, 1000)

    var buttonColor = ButtonColors(
        Color.Transparent, Color.Black,
        Color.Transparent, Color.Black
    )
    var textcolor = Color.Black
    var buttonborder = BorderStroke(2.dp, Color.Black)
    var logo = R.drawable.symphonie_branding_light

    if (isSystemInDarkTheme()) {
        logo = R.drawable.symphonie_branding_dark
        textcolor = Color.White
        buttonColor = ButtonColors(
            Color.Transparent, Color.White,
            Color.Transparent, Color.White
        )
        buttonborder = BorderStroke(2.dp, Color.White)
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

                val context = LocalContext.current
                Button(
                    shape = ShapeDefaults.ExtraLarge,
                    border = buttonborder,
                    colors = buttonColor,
                    modifier = buttonModifier,
                    onClick = {
                        context.startActivity(Intent(context, PianoActivity::class.java))
                    }
                ) {
                    Text(
                        text = "Clavier",
                        color = textcolor,
                        fontSize = 20.sp
                    )
                }

                Button(
                    shape = ShapeDefaults.ExtraLarge,
                    border = buttonborder,
                    colors = buttonColor,
                    modifier = buttonModifier,
                    onClick = {
                        navController?.navigate(ConfigScreen)
                    }) {
                    Text(
                        text = "Config",
                        color = textcolor,
                        fontSize = 20.sp
                    )
                }

                Button(
                    shape = ShapeDefaults.ExtraLarge,
                    border = buttonborder,
                    colors = buttonColor,
                    modifier = buttonModifier,
                    onClick = {
                        navController?.navigate(Colorpicker)
                    }) {
                    Text(
                        text = "ColorPicker",
                        color = textcolor,
                        fontSize = 20.sp
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
                        colors = buttonColor,
                        modifier = Modifier
                            .padding(6.dp),
                        onClick = {
                            myMQTT.reconnectToMqttBroker(context = context)
                        }) {
                        Text(
                            text = "Reconnect",
                            color = connectedcolor,
                            fontSize = 20.sp
                        )
                    }
                }

            }
        }
    }
}
