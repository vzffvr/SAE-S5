package com.example.Instrument

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.Instrument.colorpicker.ColorViewModel

@Preview
@Composable
fun HomeMenuScreen() {
    val viewModel: ColorViewModel = viewModel()

    val mqttServerUri = "tcp://10.42.0.1:1883"
    val myMQTT = MqttClientManager(mqttServerUri)

    PhareChoice(navController = null, viewmodel = viewModel, myMQTT= myMQTT)
}

@Composable
fun PhareChoice(navController: NavController?, viewmodel: ColorViewModel, myMQTT: MqttClientManager)
{
    //Background
    Box (modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
//        .paint(
//        painterResource(id = R.drawable.background2),
//        contentScale = ContentScale.FillHeight)
    ) {
        val buttonModifier: Modifier =
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(6.dp)


        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Spacer(modifier = Modifier.height(70.dp))

            //Logo
                Row (
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth(),
                ){
                    //Logo
                    Box (modifier = Modifier
                        .paint(
                        painterResource(id = R.drawable.logo_polygon),
                        contentScale = ContentScale.FillWidth)

                    )
                }

            Spacer(modifier = Modifier.height(80.dp))

            //Boutton
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){

                Button(
                    shape = ShapeDefaults.ExtraLarge,
                    border = BorderStroke(2.dp, Color.White),
                    colors = ButtonColors(
                        Color.Transparent, Color.White,
                        Color.Transparent, Color.White),
                    modifier = buttonModifier,
                    onClick = {
                        navController?.navigate(Colorpicker)
                        viewmodel.setPhare(1)
                        viewmodel.setZone(4)
                    }
                ) {
                    Text(text = "Phare Droit",
                        color= Color.White,
                        fontSize = 20.sp)
                }

                Button(
                    shape = ShapeDefaults.ExtraLarge,
                    border = BorderStroke(2.dp, Color.White),
                    colors = ButtonColors(
                        Color.Transparent, Color.White,
                        Color.Transparent, Color.White),
                    modifier = buttonModifier,
                    onClick = {
                        navController?.navigate(Colorpicker)
                        viewmodel.setPhare(2)
                        viewmodel.setZone(4)
                    }) {
                    Text(
                        text = "Phare gauche",
                        color= Color.White,
                        fontSize = 20.sp)
                }

                Button(
                    shape = ShapeDefaults.ExtraLarge,
                    border = BorderStroke(2.dp, Color.White),
                    colors = ButtonColors(
                        Color.Transparent, Color.White,
                        Color.Transparent, Color.White),
                    modifier = buttonModifier,
                    onClick = {
                        navController?.navigate(Colorpicker)
                        viewmodel.setPhare(3)
                    }) {
                    Text(
                        text = "Deux phares",
                        color= Color.White,
                        fontSize = 20.sp)
                }

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    val context = LocalContext.current
                    Button(
                        shape = ShapeDefaults.ExtraLarge,
                        border = BorderStroke(2.dp, Color.White),
                        colors = ButtonColors(
                            Color.Transparent, Color.White,
                            Color.Transparent, Color.White
                        ),
                        modifier = Modifier
                            .padding(6.dp),
                        onClick = {
                            myMQTT.reconnectToMqttBroker(context = context)
                        }) {
                        Text(
                            text = "Reconnect",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                }

            }

        }
    }
}
